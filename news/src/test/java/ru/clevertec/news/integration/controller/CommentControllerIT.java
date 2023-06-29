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
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import ru.clevertec.exceptionhandlerstarter.handler.response.impl.NotFoundElementResponse;
import ru.clevertec.news.data.builder.impl.dto.comments.CommentReqBuilder;
import ru.clevertec.news.dto.comment.CommentResponse;
import ru.clevertec.news.integration.AbstractTestContainer;
import ru.clevertec.news.util.patch.PatchRequest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
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
import static ru.clevertec.news.data.InspectorAuthorized.invalidRoleForSubscriber;
import static ru.clevertec.news.data.InspectorAuthorized.invalidUserForSubscriber;
import static ru.clevertec.news.data.InspectorAuthorized.validUserAndRoleForSubscriber;
import static ru.clevertec.news.data.reader.CommentReader.findAllDefaultPageable;
import static ru.clevertec.news.data.reader.CommentReader.findAllWithFilter;
import static ru.clevertec.news.data.reader.CommentReader.findAllWithParametersPageable;
import static ru.clevertec.news.data.reader.CommentReader.findById;
import static ru.clevertec.news.data.reader.ErrorReader.accessDeniedCommentDELETE;
import static ru.clevertec.news.data.reader.ErrorReader.accessDeniedCommentPATCH;
import static ru.clevertec.news.data.reader.ErrorReader.accessDeniedCommentPOST;
import static ru.clevertec.news.data.reader.ErrorReader.accessDeniedCommentPUT;
import static ru.clevertec.news.data.reader.ErrorReader.requiredAuthCommentDELETE;
import static ru.clevertec.news.data.reader.ErrorReader.requiredAuthCommentPATCH;
import static ru.clevertec.news.data.reader.ErrorReader.requiredAuthCommentPOST;
import static ru.clevertec.news.data.reader.ErrorReader.requiredAuthCommentPUT;

@RequiredArgsConstructor
public class CommentControllerIT extends AbstractTestContainer {

    private final MockMvc mockMvc;
    private final ObjectMapper mapper;
    private final String MAPPING = "/comments";

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
                            .param("page", "20")
                            .param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(response));
        }

        @Test
        @SneakyThrows
        void checkWithFilterShouldReturnExpected() {
            var response = mapper.writeValueAsString(findAllWithFilter());
            mockMvc.perform(get(MAPPING)
                            .param("username", "username-41")
                            .param("sort", "time"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(response));
        }

    }

    @Nested
    class CheckGetById {

        private static Stream<Long> ids() {
            return Stream.of(1L, 34L, 98L, 117L, 161L, 200L);
        }

        @Test
        @SneakyThrows
        void shouldReturnError() {
            long nonExistId = 999;
            var response = new NotFoundElementResponse();
            response.setMessage(String.format("Comment with id = %d not found", nonExistId));
            response.setStatus(404);
            mockMvc.perform(get(MAPPING + "/" + nonExistId))
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().string(mapper.writeValueAsString(response)));
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("ids")
        void shouldReturnExpected(Long id) {
            var response = mapper.writeValueAsString(findById(id));
            mockMvc.perform(get(MAPPING + "/" + id))
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
                            .withBody(validUserAndRoleForSubscriber())
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())));
            var request = CommentReqBuilder.commentReq().build();
            var response = mockMvc.perform(post(MAPPING)
                            .header(HttpHeaders.AUTHORIZATION, TOKEN)
                            .contentType(APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            var actual = mapper.readValue(response, CommentResponse.class);
            assertAll(
                    () -> assertThat(actual.id()).isNotNull(),
                    () -> assertThat(actual.time()).isNotNull(),
                    () -> assertThat(actual.username()).isEqualTo(request.username()),
                    () -> assertThat(actual.text()).isEqualTo(request.text()),
                    () -> assertThat(actual.newsId()).isEqualTo(request.newsId()));
        }

        @Test
        @SneakyThrows
        void withoutAuthorizationShouldReturnErrorAccess() {
            mockMvc.perform(post(MAPPING))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(requiredAuthCommentPOST()));
        }

        @Test
        @SneakyThrows
        void invalidAuthenticatorShouldReturnErrorAccess() {
            stubFor(WireMock.post(urlEqualTo("/auth"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(invalidRoleForSubscriber())
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())));
            mockMvc.perform(post(MAPPING)
                            .header(HttpHeaders.AUTHORIZATION, TOKEN))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(accessDeniedCommentPOST()));
        }

    }

    @Nested
    @WireMockTest(httpPort = 8081)
    class CheckPut {

        @Test
        @SneakyThrows
        void nonExistShouldReturnClientError() {
            long nonExistId = 999L;
            stubFor(WireMock.post(urlEqualTo("/auth"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(validUserAndRoleForSubscriber())
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())));
            var request = CommentReqBuilder.commentReq().build();
            mockMvc.perform(put(MAPPING + "/" + nonExistId)
                            .header(HttpHeaders.AUTHORIZATION, TOKEN)
                            .contentType(APPLICATION_JSON)
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
                            .withBody(validUserAndRoleForSubscriber())
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())));
            var request = CommentReqBuilder.commentReq().build();
            var response = mockMvc.perform(put(MAPPING + "/" + id)
                            .header(HttpHeaders.AUTHORIZATION, TOKEN)
                            .contentType(APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            var actual = mapper.readValue(response, CommentResponse.class);
            assertAll(
                    () -> assertThat(actual.id()).isEqualTo(id),
                    () -> assertThat(actual.time()).isNotNull(),
                    () -> assertThat(actual.username()).isEqualTo(request.username()),
                    () -> assertThat(actual.text()).isEqualTo(request.text()),
                    () -> assertThat(actual.newsId()).isEqualTo(request.newsId()));
        }

        @Test
        @SneakyThrows
        void withoutAuthorizationShouldReturnErrorAccess() {
            mockMvc.perform(put(MAPPING + "/1"))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(requiredAuthCommentPUT()));
        }

        @Test
        @SneakyThrows
        void invalidRoleShouldReturnErrorAccess() {
            stubFor(WireMock.post(urlEqualTo("/auth"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(invalidRoleForSubscriber())
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())));
            mockMvc.perform(put(MAPPING + "/1")
                            .header(HttpHeaders.AUTHORIZATION, TOKEN))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(accessDeniedCommentPUT()));
        }

        @Test
        @SneakyThrows
        void invalidUserShouldReturnErrorAccess() {
            stubFor(WireMock.post(urlEqualTo("/auth"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(invalidUserForSubscriber())
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())));
            var request = CommentReqBuilder.commentReq().build();
            mockMvc.perform(put(MAPPING + "/1")
                            .header(HttpHeaders.AUTHORIZATION, TOKEN)
                            .contentType(APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(accessDeniedCommentPUT()));
        }

    }

    @Nested
    @WireMockTest(httpPort = 8081)
    class CheckPatch {

        private static Stream<Arguments> patchField() {
            return Stream.of(
                    Arguments.of("username", "\"newUsername\""),
                    Arguments.of("text", "\"newText\""));
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("patchField")
        void shouldUpdateField(String field, String value) {
            stubFor(WireMock.post(urlEqualTo("/auth"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(validUserAndRoleForSubscriber())
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())));
            var request = new PatchRequest(field, value);
            var expected = mapper.readValue(value, String.class);
            mockMvc.perform(patch(MAPPING + "/1")
                            .header(HttpHeaders.AUTHORIZATION, TOKEN)
                            .contentType(APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath(field).value(expected));
        }

        @Test
        @SneakyThrows
        void withoutAuthorizationShouldReturnErrorAccess() {
            mockMvc.perform(patch(MAPPING + "/1"))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(requiredAuthCommentPATCH()));
        }

        @Test
        @SneakyThrows
        void invalidRoleShouldReturnErrorAccess() {
            stubFor(WireMock.post(urlEqualTo("/auth"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(invalidRoleForSubscriber())
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())));
            mockMvc.perform(patch(MAPPING + "/1")
                            .header(HttpHeaders.AUTHORIZATION, TOKEN))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(accessDeniedCommentPATCH()));
        }

        @Test
        @SneakyThrows
        void invalidUserShouldReturnErrorAccess() {
            stubFor(WireMock.post(urlEqualTo("/auth"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(invalidUserForSubscriber())
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())));
            var request = new PatchRequest("field", "value");
            mockMvc.perform(patch(MAPPING + "/1")
                            .header(HttpHeaders.AUTHORIZATION, TOKEN)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(accessDeniedCommentPATCH()));
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
                            .withBody(validUserAndRoleForSubscriber())
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())));
            mockMvc.perform(delete(MAPPING + "/" + nonExistId)
                            .header(HttpHeaders.AUTHORIZATION, TOKEN))
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @SneakyThrows
        void shouldDelete() {
            stubFor(WireMock.post(urlEqualTo("/auth"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(validUserAndRoleForSubscriber())
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
                            .withBody(validUserAndRoleForSubscriber())
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())));
            mockMvc.perform(delete(MAPPING + "/1"))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(requiredAuthCommentDELETE()));
        }

        @Test
        @SneakyThrows
        void invalidRoleShouldReturnErrorAccess() {
            stubFor(WireMock.post(urlEqualTo("/auth"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(invalidRoleForSubscriber())
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())));
            mockMvc.perform(delete(MAPPING + "/1")
                            .header(HttpHeaders.AUTHORIZATION, TOKEN))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(accessDeniedCommentDELETE()));
        }

        @Test
        @SneakyThrows
        void invalidUserShouldReturnErrorAccess() {
            stubFor(WireMock.post(urlEqualTo("/auth"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(invalidUserForSubscriber())
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())));
            mockMvc.perform(delete(MAPPING + "/1")
                            .header(HttpHeaders.AUTHORIZATION, TOKEN))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(accessDeniedCommentDELETE()));
        }

        @Test
        @SneakyThrows
        void errorOnServerShouldReturnError() {
            stubFor(WireMock.post(urlEqualTo("/auth"))
                    .willReturn(serverError().withStatus(500)));
            mockMvc.perform(delete(MAPPING + "/1")
                            .header(HttpHeaders.AUTHORIZATION, TOKEN))
                    .andExpect(status().isForbidden());
        }

    }

}