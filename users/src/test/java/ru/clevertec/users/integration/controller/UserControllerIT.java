package ru.clevertec.users.integration.controller;

import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import ru.clevertec.users.data.builder.impl.dto.users.UserReqBuilder;
import ru.clevertec.users.integration.AbstractTestContainer;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.clevertec.users.data.reader.ErrorReader.invalidRole;
import static ru.clevertec.users.data.reader.ErrorReader.notFoundUserById;
import static ru.clevertec.users.data.reader.TokenReader.describersTokenWithPrefix;
import static ru.clevertec.users.data.reader.TokenReader.journalistsTokenWithPrefix;
import static ru.clevertec.users.data.reader.TokenReader.validWithPrefix;
import static ru.clevertec.users.data.reader.UserReader.findAllUserResponseDefault;
import static ru.clevertec.users.data.reader.UserReader.findAllWithFilter;
import static ru.clevertec.users.data.reader.UserReader.findAllWithParametersPageable;
import static ru.clevertec.users.data.reader.UserReader.findById;

@RequiredArgsConstructor
class UserControllerIT extends AbstractTestContainer {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String MAPPING = "/users";
    private final MockMvc mockMvc;

    @Nested
    class CheckFindAll {

        private static Stream<String> rolesToken() {
            return Stream.of(journalistsTokenWithPrefix(), describersTokenWithPrefix());
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("rolesToken")
        void noAdminShouldReturnError(String token) {
            var response = mapper.writeValueAsString(invalidRole());
            mockMvc.perform(get(MAPPING)
                            .header(HttpHeaders.AUTHORIZATION, token))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(response));
        }

        @Test
        @SneakyThrows
        void checkDefaultPageableShouldReturnExpected() {
            var response = mapper.writeValueAsString(findAllUserResponseDefault());
            mockMvc.perform(get(MAPPING)
                            .header(HttpHeaders.AUTHORIZATION, validWithPrefix()))
                    .andExpect(status().isOk())
                    .andExpect(content().string(response));
        }

        @Test
        @SneakyThrows
        void checkWithParametersPageableShouldReturnExpected() {
            var response = mapper.writeValueAsString(findAllWithParametersPageable());
            mockMvc.perform(get(MAPPING)
                            .param("page", "1")
                            .param("size", "2")
                            .header(HttpHeaders.AUTHORIZATION, validWithPrefix()))
                    .andExpect(status().isOk())
                    .andExpect(content().string(response));
        }

        @Test
        @SneakyThrows
        void checkWithFilterShouldReturnExpected() {
            var response = mapper.writeValueAsString(findAllWithFilter());
            mockMvc.perform(get(MAPPING)
                            .param("firstname", "maksim")
                            .header(HttpHeaders.AUTHORIZATION, validWithPrefix()))
                    .andExpect(status().isOk())
                    .andExpect(content().string(response));
        }

    }

    @Nested
    class CheckGetById {

        private static Stream<String> rolesToken() {
            return Stream.of(journalistsTokenWithPrefix(), describersTokenWithPrefix());
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("rolesToken")
        void noAdminShouldReturnError(String token) {
            mockMvc.perform(get(MAPPING + "/3")
                            .header(HttpHeaders.AUTHORIZATION, token))
                    .andExpect(status().isForbidden());
        }

        @Test
        @SneakyThrows
        void shouldReturnError() {
            long nonExistId = 999;
            String response = mapper.writeValueAsString(notFoundUserById());
            mockMvc.perform(get(MAPPING + "/" + nonExistId)
                            .header(HttpHeaders.AUTHORIZATION, validWithPrefix()))
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().string(response));
        }

        @SneakyThrows
        @ParameterizedTest
        @ValueSource(longs = {1, 2, 3})
        void shouldReturnExpected(long id) {
            String response = mapper.writeValueAsString(findById(id));
            mockMvc.perform(get(MAPPING + "/" + id)
                            .header(HttpHeaders.AUTHORIZATION, validWithPrefix()))
                    .andExpect(status().isOk())
                    .andExpect(content().string(response));
        }

    }

    @Nested
    class CheckPut {

        @Test
        @SneakyThrows
        void nonExistShouldReturnError() {
            long nonExistId = 999L;
            var request = UserReqBuilder.userReq().build();
            mockMvc.perform(put(MAPPING + "/" + nonExistId)
                            .header(HttpHeaders.AUTHORIZATION, validWithPrefix())
                            .contentType(APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @SneakyThrows
        void withoutAuthorizationShouldReturnErrorAccess() {
            var request = UserReqBuilder.userReq().build();
            mockMvc.perform(put(MAPPING + "/1")
                            .contentType(APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }

        @SneakyThrows
        @ParameterizedTest
        @ValueSource(longs = {2, 3})
        void notOwnerShouldReturnErrorAccess(long id) {
            var request = UserReqBuilder.userReq().build();
            mockMvc.perform(put(MAPPING + "/" + id)
                            .contentType(APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, validWithPrefix())
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @SneakyThrows
        void shouldUpdateAndReturnExpected() {
            var request = UserReqBuilder.userReq().build();
            mockMvc.perform(put(MAPPING + "/1")
                            .header(HttpHeaders.AUTHORIZATION, validWithPrefix())
                            .contentType(APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(jsonPath("email").value(request.email()))
                    .andExpect(jsonPath("firstname").value(request.firstname()))
                    .andExpect(jsonPath("lastname").value(request.lastname()))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
        }

    }

    @Nested
    class CheckDelete {

        @Test
        @SneakyThrows
        void withoutAuthorizationShouldReturnErrorAccess() {
            mockMvc.perform(delete(MAPPING + "/1"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @SneakyThrows
        void nonExistShouldReturnError() {
            long nonExistId = 999L;
            String response = mapper.writeValueAsString(notFoundUserById());
            mockMvc.perform(delete(MAPPING + "/" + nonExistId)
                            .header(HttpHeaders.AUTHORIZATION, validWithPrefix()))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(response));
        }


        @SneakyThrows
        @ParameterizedTest
        @ValueSource(longs = {1, 2, 3})
        void adminShouldDeleteEveryThing(long id) {
            mockMvc.perform(delete(MAPPING + "/" + id)
                            .header(HttpHeaders.AUTHORIZATION, validWithPrefix()))
                    .andExpect(status().isNoContent());
        }

        private static Stream<Arguments> idAndStatus() {
            return Stream.of(Arguments.of(1L, HttpStatus.FORBIDDEN.value()),
                    Arguments.of(2L, HttpStatus.NO_CONTENT.value()),
                    Arguments.of(3L, HttpStatus.FORBIDDEN.value()));
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("idAndStatus")
        void subscriberShouldDeleteOnlyOwn(long id, int status) {
            mockMvc.perform(delete(MAPPING + "/" + id)
                            .header(HttpHeaders.AUTHORIZATION, journalistsTokenWithPrefix()))
                    .andExpect(status().is(status));
        }

    }

}