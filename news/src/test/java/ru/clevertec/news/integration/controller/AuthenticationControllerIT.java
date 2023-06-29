package ru.clevertec.news.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.clevertec.news.client.ExternalUserApi;
import ru.clevertec.news.config.SecurityConfig;
import ru.clevertec.news.controller.AuthenticationController;
import ru.clevertec.news.data.builder.impl.dto.auth.LoginDtoBuilder;
import ru.clevertec.news.data.builder.impl.dto.auth.RegisterDtoBuilder;
import ru.clevertec.news.data.builder.impl.dto.users.UserResBuilder;
import ru.clevertec.news.dto.auth.AuthDto;
import ru.clevertec.news.dto.auth.LoginDto;
import ru.clevertec.news.dto.auth.RegisterDto;
import ru.clevertec.news.dto.auth.TokenDto;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.clevertec.news.data.InspectorAuthorized.TOKEN;

@Import(SecurityConfig.class)
@WebMvcTest(AuthenticationController.class)
public class AuthenticationControllerIT {

    private final String MAPPING = "/auth";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ExternalUserApi externalUserApi;

    private static Stream<Integer> statuses() {
        return Stream.of(100, 200, 300, 400, 500);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("statuses")
    public void checkLoginShouldReturnExpected(int status) {
        LoginDto loginDto = LoginDtoBuilder.loginDto().build();
        var externalResponse = ResponseEntity.status(status).body(new TokenDto(TOKEN));
        int expectedCode = externalResponse.getStatusCode().value();
        String expectedBody = mapper.writeValueAsString(externalResponse.getBody());
        doReturn(externalResponse).when(externalUserApi).login(loginDto);
        mockMvc.perform(post(MAPPING + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginDto)))
                .andExpect(status().is(expectedCode))
                .andExpect(content().string(expectedBody));
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("statuses")
    public void checkRegisterShouldReturnExpected(int status) {
        RegisterDto registerDto = RegisterDtoBuilder.registerDto().build();
        var externalResponse = ResponseEntity.status(status).body(new TokenDto(TOKEN));
        int expectedCode = externalResponse.getStatusCode().value();
        String expectedBody = mapper.writeValueAsString(externalResponse.getBody());
        doReturn(externalResponse).when(externalUserApi).register(registerDto);
        mockMvc.perform(post(MAPPING + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registerDto)))
                .andExpect(status().is(expectedCode))
                .andExpect(content().string(expectedBody));
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("statuses")
    public void checkAboutMeShouldReturnExpected(int status) {
        AuthDto authDto = new AuthDto("username", List.of("SUBSCRIBER"));
        var externalAuthDto = ResponseEntity.status(status).body(authDto);
        var externalUserResponse = ResponseEntity.status(status).body(UserResBuilder.userRes().build());
        int expectedCode = externalUserResponse.getStatusCode().value();
        String expectedBody = mapper.writeValueAsString(externalUserResponse.getBody());
        doReturn(externalAuthDto).when(externalUserApi).getUserDetails(TOKEN);
        doReturn(externalUserResponse).when(externalUserApi).aboutMe(TOKEN);
        mockMvc.perform(post(MAPPING + "/about_me")
                        .header(HttpHeaders.AUTHORIZATION, TOKEN))
                .andExpect(status().is(expectedCode))
                .andExpect(content().string(expectedBody));
    }

}