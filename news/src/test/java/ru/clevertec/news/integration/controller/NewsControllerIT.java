package ru.clevertec.news.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import ru.clevertec.exceptionhandlerstarter.handler.response.impl.NotFoundElementResponse;
import ru.clevertec.news.data.builder.impl.dto.news.NewsReqBuilder;
import ru.clevertec.news.dto.news.NewsResponse;
import ru.clevertec.news.integration.AbstractTestContainer;
import ru.clevertec.news.util.patch.PatchRequest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.clevertec.news.data.InspectorAuthorized.TOKEN;
import static ru.clevertec.news.data.InspectorAuthorized.invalidRoleForJournalist;
import static ru.clevertec.news.data.InspectorAuthorized.invalidUserForJournalist;
import static ru.clevertec.news.data.InspectorAuthorized.validUserAndRoleForJournalist;
import static ru.clevertec.news.data.reader.ErrorReader.accessDeniedNewsDELETE;
import static ru.clevertec.news.data.reader.ErrorReader.accessDeniedNewsPATCH;
import static ru.clevertec.news.data.reader.ErrorReader.accessDeniedNewsPOST;
import static ru.clevertec.news.data.reader.ErrorReader.accessDeniedNewsPUT;
import static ru.clevertec.news.data.reader.ErrorReader.requiredAuthNewsDELETE;
import static ru.clevertec.news.data.reader.ErrorReader.requiredAuthNewsPATCH;
import static ru.clevertec.news.data.reader.ErrorReader.requiredAuthNewsPOST;
import static ru.clevertec.news.data.reader.ErrorReader.requiredAuthNewsPUT;
import static ru.clevertec.news.data.reader.NewsReader.findAllDefaultPageable;
import static ru.clevertec.news.data.reader.NewsReader.findAllWithFilter;
import static ru.clevertec.news.data.reader.NewsReader.findAllWithParametersPageable;
import static ru.clevertec.news.data.reader.NewsReader.findByIdDefaultPageable;
import static ru.clevertec.news.data.reader.NewsReader.findByIdWithParametersPageable;

@RequiredArgsConstructor
public class NewsControllerIT extends AbstractTestContainer {

    private final MockMvc mockMvc;
    private final ObjectMapper mapper;
    private final String MAPPING = "/news";

    @Nested
    class CheckGetAll {

        @Test
        @SneakyThrows
        void checkDefaultPageableShouldReturnExpected() {
            var response = mapper.writeValueAsString(findAllDefaultPageable());
            mockMvc.perform(get(MAPPING))
                    .andExpect(status().isOk())
                    .andExpect(content().string(response));
        }

        @Test
        @SneakyThrows
        void checkWithParametersPageableShouldReturnExpected() {
            var response = mapper.writeValueAsString(findAllWithParametersPageable());
            mockMvc.perform(get(MAPPING)
                            .param("page", "2")
                            .param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(response));
        }

        @Test
        @SneakyThrows
        void checkWithFilterShouldReturnExpected() {
            var response = mapper.writeValueAsString(findAllWithFilter());
            mockMvc.perform(get(MAPPING)
                            .param("title", "Title-3"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(response));
        }

    }

    @Nested
    class CheckGetById {

        private static Stream<Long> ids() {
            return Stream.of(1L, 8L, 13L, 20L);
        }

        @Test
        @SneakyThrows
        void shouldReturnError() {
            long nonExistId = 999;
            var response = new NotFoundElementResponse();
            response.setMessage(String.format("News with id = %d not found", nonExistId));
            response.setStatus(404);
            mockMvc.perform(get(MAPPING + "/" + nonExistId))
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().string(mapper.writeValueAsString(response)));
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("ids")
        void defaultPageableShouldReturnExpected(Long id) {
            var response = mapper.writeValueAsString(findByIdDefaultPageable(id));
            mockMvc.perform(get(MAPPING + "/" + id))
                    .andExpect(status().isOk())
                    .andExpect(content().string(response));
        }

        @Test
        @SneakyThrows
        void withParametersPageableShouldReturnExpected() {
            var response = mapper.writeValueAsString(findByIdWithParametersPageable());
            mockMvc.perform(get(MAPPING + "/1")
                            .param("page", "3")
                            .param("size", "2"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(response));
        }

    }

    @Nested
    @WireMockTest(httpPort = 8081)
    class CheckPost {

        @Test
        @SneakyThrows
        void shouldSaveAndReturnExpected() {
            stubFor(WireMock.post(urlEqualTo("/auth"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(validUserAndRoleForJournalist())
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())));
            var request = NewsReqBuilder.newsReq().build();
            var response = mockMvc.perform(post(MAPPING)
                            .header(HttpHeaders.AUTHORIZATION, TOKEN)
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            var actual = mapper.readValue(response, NewsResponse.class);
            assertAll(
                    () -> assertThat(actual.id()).isNotNull(),
                    () -> assertThat(actual.time()).isNotNull(),
                    () -> assertThat(actual.title()).isEqualTo(request.title()),
                    () -> assertThat(actual.text()).isEqualTo(request.text()));
        }

        @Test
        @SneakyThrows
        void withoutAuthorizationShouldReturnErrorAccess() {
            mockMvc.perform(post(MAPPING))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(requiredAuthNewsPOST()));
        }

        @Test
        @SneakyThrows
        void invalidAuthenticatorShouldReturnErrorAccess() {
            stubFor(WireMock.post(urlEqualTo("/auth"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(invalidRoleForJournalist())
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())));
            mockMvc.perform(post(MAPPING)
                            .header(HttpHeaders.AUTHORIZATION, TOKEN))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(accessDeniedNewsPOST()));
        }

    }

    @Nested
    @WireMockTest(httpPort = 8081)
    class CheckPut {

        @Test
        @SneakyThrows
        void nonExistShouldReturnClientError() {
            long id = 999L;
            stubFor(WireMock.post(urlEqualTo("/auth"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(validUserAndRoleForJournalist())
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())));
            var request = NewsReqBuilder.newsReq().build();
            mockMvc.perform(put(MAPPING + "/" + id)
                            .header(HttpHeaders.AUTHORIZATION, TOKEN)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @SneakyThrows
        void shouldUpdateAndReturnExpected() {
            long id = 1L;
            stubFor(WireMock.post(urlEqualTo("/auth"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(validUserAndRoleForJournalist())
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())));
            var request = NewsReqBuilder.newsReq().build();
            var response = mockMvc.perform(put(MAPPING + "/" + id)
                            .header(HttpHeaders.AUTHORIZATION, TOKEN)
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            var actual = mapper.readValue(response, NewsResponse.class);
            assertAll(
                    () -> assertThat(actual.id()).isEqualTo(id),
                    () -> assertThat(actual.time()).isNotNull(),
                    () -> assertThat(actual.title()).isEqualTo(request.title()),
                    () -> assertThat(actual.text()).isEqualTo(request.text()));
        }

        @Test
        @SneakyThrows
        void withoutAuthorizationShouldReturnErrorAccess() {
            mockMvc.perform(put(MAPPING + "/1"))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(requiredAuthNewsPUT()));
        }

        @Test
        @SneakyThrows
        void invalidRoleShouldReturnErrorAccess() {
            stubFor(WireMock.post(urlEqualTo("/auth"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(invalidRoleForJournalist())
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())));
            mockMvc.perform(put(MAPPING + "/1")
                            .header(HttpHeaders.AUTHORIZATION, TOKEN))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(accessDeniedNewsPUT()));
        }

        @Test
        @SneakyThrows
        void invalidUserShouldReturnErrorAccess() {
            stubFor(WireMock.post(urlEqualTo("/auth"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(invalidUserForJournalist())
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())));
            var request = NewsReqBuilder.newsReq().build();
            mockMvc.perform(put(MAPPING + "/1")
                            .header(HttpHeaders.AUTHORIZATION, TOKEN)
                            .contentType(APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(accessDeniedNewsPUT()));
        }

    }

    @Nested
    @WireMockTest(httpPort = 8081)
    class CheckPatch {

        private static Stream<Arguments> patchField() {
            return Stream.of(
                    Arguments.of("title", "\"newTitle\""),
                    Arguments.of("text", "\"newText\""));
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("patchField")
        void shouldUpdateField(String field, String value) {
            stubFor(WireMock.post(urlEqualTo("/auth"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(validUserAndRoleForJournalist())
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())));
            var request = new PatchRequest(field, value);
            var expected = mapper.readValue(value, String.class);
            mockMvc.perform(patch(MAPPING + "/1")
                            .header(HttpHeaders.AUTHORIZATION, TOKEN)
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath(field).value(expected));
        }

        @Test
        @SneakyThrows
        void withoutAuthorizationShouldReturnErrorAccess() {
            mockMvc.perform(patch(MAPPING + "/1"))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(requiredAuthNewsPATCH()));
        }

        @Test
        @SneakyThrows
        void invalidRoleShouldReturnErrorAccess() {
            stubFor(WireMock.post(urlEqualTo("/auth"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(invalidRoleForJournalist())
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())));
            mockMvc.perform(patch(MAPPING + "/1")
                            .header(HttpHeaders.AUTHORIZATION, TOKEN))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(accessDeniedNewsPATCH()));
        }

        @Test
        @SneakyThrows
        void invalidUserShouldReturnErrorAccess() {
            stubFor(WireMock.post(urlEqualTo("/auth"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(invalidUserForJournalist())
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())));
            var request = new PatchRequest("field", "value");
            mockMvc.perform(patch(MAPPING + "/1")
                            .header(HttpHeaders.AUTHORIZATION, TOKEN)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(accessDeniedNewsPATCH()));
        }

    }

    @Nested
    @WireMockTest(httpPort = 8081)
    class CheckDelete {

        @Test
        @SneakyThrows
        void shouldReturnError() {
            long nonExistId = 999L;
            stubFor(WireMock.post(urlEqualTo("/auth"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(validUserAndRoleForJournalist())
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())));
            mockMvc.perform(delete(MAPPING + "/" + nonExistId)
                            .header(HttpHeaders.AUTHORIZATION, TOKEN))
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @SneakyThrows
        @WithMockUser(username = "username-1", authorities = "JOURNALIST")
        void shouldDelete() {
            stubFor(WireMock.post(urlEqualTo("/auth"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(validUserAndRoleForJournalist())
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())));
            mockMvc.perform(delete(MAPPING + "/1")
                            .header(HttpHeaders.AUTHORIZATION, TOKEN))
                    .andExpect(status().isNoContent());
        }

        @Test
        @SneakyThrows
        void withoutAuthorizationShouldReturnErrorAccess() {
            stubFor(WireMock.post(urlEqualTo("/auth"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(validUserAndRoleForJournalist())
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())));
            mockMvc.perform(delete(MAPPING + "/1"))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(requiredAuthNewsDELETE()));
        }

        @Test
        @SneakyThrows
        void invalidRoleShouldReturnErrorAccess() {
            stubFor(WireMock.post(urlEqualTo("/auth"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(invalidRoleForJournalist())
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())));
            mockMvc.perform(delete(MAPPING + "/1")
                            .header(HttpHeaders.AUTHORIZATION, TOKEN))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(accessDeniedNewsDELETE()));
        }

        @Test
        @SneakyThrows
        void invalidUserShouldReturnErrorAccess() {
            stubFor(WireMock.post(urlEqualTo("/auth"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(invalidUserForJournalist())
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())));
            mockMvc.perform(delete(MAPPING + "/1")
                            .header(HttpHeaders.AUTHORIZATION, TOKEN))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(accessDeniedNewsDELETE()));
        }

    }

}