package ru.clevertec.news.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.loggingstarter.annotation.Logging;
import ru.clevertec.news.client.ExternalUserApi;
import ru.clevertec.news.controller.openapi.AuthenticationOpenApi;
import ru.clevertec.news.dto.auth.LoginDto;
import ru.clevertec.news.dto.auth.TokenDto;
import ru.clevertec.news.dto.auth.RegisterDto;
import ru.clevertec.news.dto.users.UserResponse;

@Logging
@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthenticationController implements AuthenticationOpenApi {

    private final ExternalUserApi externalUserApi;

    @Override
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginDto request) {
        return externalUserApi.login(request);
    }

    @Override
    @PostMapping("/register")
    public ResponseEntity<TokenDto> register(@RequestBody RegisterDto request) {
        return externalUserApi.register(request);
    }

    @Override
    @PostMapping("/about_me")
    public ResponseEntity<UserResponse> aboutMe(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token) {
        return externalUserApi.aboutMe(token);
    }

}