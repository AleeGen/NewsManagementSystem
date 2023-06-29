package ru.clevertec.users.integration.controller;

import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.clevertec.users.data.builder.impl.dto.auth.LoginDtoBuilder;
import ru.clevertec.users.data.builder.impl.dto.auth.RegisterDtoBuilder;
import ru.clevertec.users.dto.auth.AuthDto;
import ru.clevertec.users.dto.auth.LoginDto;
import ru.clevertec.users.dto.auth.RegisterDto;
import ru.clevertec.users.integration.AbstractTestContainer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.clevertec.users.data.reader.ErrorReader.invalidPassword;
import static ru.clevertec.users.data.reader.ErrorReader.invalidSyntax;
import static ru.clevertec.users.data.reader.ErrorReader.invalidUser;
import static ru.clevertec.users.data.reader.ErrorReader.notFoundUserByUsername;
import static ru.clevertec.users.data.reader.ErrorReader.requiredAuthForAboutMe;
import static ru.clevertec.users.data.reader.ErrorReader.requiredAuthForAuth;
import static ru.clevertec.users.data.reader.TokenReader.invalidBySyntaxWithPrefix;
import static ru.clevertec.users.data.reader.TokenReader.invalidByTimeWithPrefix;
import static ru.clevertec.users.data.reader.TokenReader.invalidByUserWithPrefix;
import static ru.clevertec.users.data.reader.TokenReader.validWithPrefix;
import static ru.clevertec.users.data.reader.UserReader.findById;

@RequiredArgsConstructor
public class AuthControllerIT extends AbstractTestContainer {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String MAPPING = "/auth";
    private final MockMvc mockMvc;

    @Nested
    class CheckAuth {

        @SneakyThrows
        private static Stream<Arguments> tokenAndError() {
            return Stream.of(
                    Arguments.of(
                            invalidBySyntaxWithPrefix(),
                            mapper.writeValueAsString(invalidSyntax())),
                    Arguments.of(
                            invalidByUserWithPrefix(),
                            mapper.writeValueAsString(invalidUser())),
                    Arguments.of("", mapper.writeValueAsString(requiredAuthForAuth())));
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("tokenAndError")
        void invalidTokenShouldReturnError(String token, String response) {
            mockMvc.perform(post(MAPPING)
                            .header(HttpHeaders.AUTHORIZATION, token))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(response));
        }

        @Test
        @SneakyThrows
        void invalidTimeShouldReturnError() {
            mockMvc.perform(post(MAPPING)
                            .header(HttpHeaders.AUTHORIZATION, invalidByTimeWithPrefix()))
                    .andExpect(status().isForbidden());
        }

        @Test
        @SneakyThrows
        void shouldReturnExpected() {
            String response = mapper.writeValueAsString(new AuthDto("leon", List.of("ADMIN")));
            mockMvc.perform(post(MAPPING)
                            .header(HttpHeaders.AUTHORIZATION, validWithPrefix()))
                    .andExpect(status().isOk())
                    .andExpect(content().string(response));
        }

    }

    @Nested
    class CheckLogin {

        @SneakyThrows
        private static Stream<Arguments> loginAndError() {
            return Stream.of(
                    Arguments.of(
                            mapper.writeValueAsString(LoginDtoBuilder.loginDto().build()),
                            mapper.writeValueAsString(notFoundUserByUsername())),
                    Arguments.of(
                            mapper.writeValueAsString(LoginDtoBuilder.loginDto().withUsername("leon").build()),
                            mapper.writeValueAsString(invalidPassword())));
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("loginAndError")
        void invalidLoginReturnError(String loginDto, String response) {
            mockMvc.perform(post(MAPPING + "/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginDto))
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().string(response));
        }

        @Test
        @SneakyThrows
        void shouldReturnExpected() {
            LoginDto loginDto = LoginDtoBuilder.loginDto().withUsername("leon").withPassword("pass").build();
            mockMvc.perform(post(MAPPING + "/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(loginDto)))
                    .andExpect(status().isOk());
        }

    }

    @Nested
    class CheckRegister {

        @Test
        @SneakyThrows
        void existingUsernameShouldReturnError() {
            RegisterDto registerWithExistingUsername = RegisterDtoBuilder.registerDto().withUsername("leon").build();
            mockMvc.perform(post(MAPPING + "/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(registerWithExistingUsername)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @SneakyThrows
        void shouldReturnExpected() {
            RegisterDto register = RegisterDtoBuilder.registerDto().withUsername("newUsername").build();
            String response = mockMvc.perform(post(MAPPING + "/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(register)))
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getContentAsString();
            assertNotNull(response);
        }

    }

    @Nested
    class CheckAboutMe {

        @SneakyThrows
        private static Stream<Arguments> tokenAndError() {
            return Stream.of(
                    Arguments.of(
                            invalidBySyntaxWithPrefix(),
                            mapper.writeValueAsString(invalidSyntax())),
                    Arguments.of(
                            invalidByUserWithPrefix(),
                            mapper.writeValueAsString(invalidUser())),
                    Arguments.of("", mapper.writeValueAsString(requiredAuthForAboutMe())));
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("tokenAndError")
        void invalidTokenShouldReturnError(String token, String response) {
            mockMvc.perform(post(MAPPING + "/about_me")
                            .header(HttpHeaders.AUTHORIZATION, token))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(response));
        }

        @Test
        @SneakyThrows
        void invalidTimeShouldReturnError() {
            mockMvc.perform(post(MAPPING + "/about_me")
                            .header(HttpHeaders.AUTHORIZATION, invalidByTimeWithPrefix()))
                    .andExpect(status().isForbidden());
        }

        @Test
        @SneakyThrows
        void shouldReturnExpected() {
            String response = mapper.writeValueAsString(findById(1L));
            mockMvc.perform(post(MAPPING + "/about_me")
                            .header(HttpHeaders.AUTHORIZATION, validWithPrefix()))
                    .andExpect(status().isOk())
                    .andExpect(content().string(response));
        }

    }

}