package ru.clevertec.users.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.clevertec.users.config.SecurityConfig;
import ru.clevertec.users.data.builder.impl.dto.auth.LoginDtoBuilder;
import ru.clevertec.users.data.builder.impl.dto.auth.RegisterDtoBuilder;
import ru.clevertec.users.dto.auth.LoginDto;
import ru.clevertec.users.dto.auth.RegisterDto;
import ru.clevertec.users.service.AuthenticationService;
import ru.clevertec.users.service.JwtService;
import ru.clevertec.users.service.UserService;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(AuthController.class)
@MockBeans({
        @MockBean(AuthenticationService.class),
        @MockBean(UserService.class),
        @MockBean(JwtService.class)
})
class AuthControllerTest {

    private final String MAPPING = "/auth";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Nested
    class CheckValidation {

        private static Stream<LoginDto> negativeLogin() {
            return Stream.of(
                    LoginDtoBuilder.loginDto().withUsername(null).build(),
                    LoginDtoBuilder.loginDto().withUsername("").build(),
                    LoginDtoBuilder.loginDto().withUsername(" ").build(),
                    LoginDtoBuilder.loginDto().withPassword(null).build(),
                    LoginDtoBuilder.loginDto().withPassword("").build(),
                    LoginDtoBuilder.loginDto().withPassword(" ").build());
        }

        private static Stream<LoginDto> positiveLogin() {
            return Stream.of(
                    LoginDtoBuilder.loginDto().withUsername("username").build(),
                    LoginDtoBuilder.loginDto().withPassword("password").build());
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("negativeLogin")
        void checkLoginShouldReturnError(LoginDto request) {
            mockMvc.perform(post(MAPPING + "/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("positiveLogin")
        void checkLoginShouldAccept(LoginDto request) {
            mockMvc.perform(post(MAPPING + "/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }

        private static Stream<RegisterDto> negativeRegister() {
            StringBuilder charactersMore25 = new StringBuilder();
            IntStream.range(0, 26).forEach(i -> charactersMore25.append("."));
            return Stream.of(
                    RegisterDtoBuilder.registerDto().withUsername(null).build(),
                    RegisterDtoBuilder.registerDto().withUsername("").build(),
                    RegisterDtoBuilder.registerDto().withUsername(" ").build(),
                    RegisterDtoBuilder.registerDto().withUsername(charactersMore25.toString()).build(),
                    RegisterDtoBuilder.registerDto().withPassword(null).build(),
                    RegisterDtoBuilder.registerDto().withPassword("").build(),
                    RegisterDtoBuilder.registerDto().withPassword(" ").build(),
                    RegisterDtoBuilder.registerDto().withPassword("aBcD123").build(),
                    RegisterDtoBuilder.registerDto().withPassword("afjbAtiwcA").build(),
                    RegisterDtoBuilder.registerDto().withPassword("1234567890").build(),
                    RegisterDtoBuilder.registerDto().withPassword(charactersMore25.toString()).build(),
                    RegisterDtoBuilder.registerDto().withFirstname(null).build(),
                    RegisterDtoBuilder.registerDto().withFirstname("").build(),
                    RegisterDtoBuilder.registerDto().withFirstname(" ").build(),
                    RegisterDtoBuilder.registerDto().withFirstname(charactersMore25.toString()).build(),
                    RegisterDtoBuilder.registerDto().withLastname(null).build(),
                    RegisterDtoBuilder.registerDto().withLastname("").build(),
                    RegisterDtoBuilder.registerDto().withLastname(" ").build(),
                    RegisterDtoBuilder.registerDto().withLastname(charactersMore25.toString()).build(),
                    RegisterDtoBuilder.registerDto().withEmail(null).build(),
                    RegisterDtoBuilder.registerDto().withEmail("").build(),
                    RegisterDtoBuilder.registerDto().withEmail(" ").build(),
                    RegisterDtoBuilder.registerDto().withEmail("email-gmail.com").build());
        }

        private static Stream<RegisterDto> positiveRegister() {
            return Stream.of(
                    RegisterDtoBuilder.registerDto().withUsername("username").build(),
                    RegisterDtoBuilder.registerDto().withPassword("PASSword123").build(),
                    RegisterDtoBuilder.registerDto().withFirstname("firstname").build(),
                    RegisterDtoBuilder.registerDto().withLastname("lastname").build(),
                    RegisterDtoBuilder.registerDto().withEmail("email@gmail.com").build());
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("negativeRegister")
        void checkRegisterShouldReturnError(RegisterDto request) {
            mockMvc.perform(post(MAPPING + "/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("positiveRegister")
        void checkRegisterShouldAccept(RegisterDto request) {
            mockMvc.perform(post(MAPPING + "/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

    }

}