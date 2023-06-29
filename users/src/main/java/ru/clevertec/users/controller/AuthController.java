package ru.clevertec.users.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.loggingstarter.annotation.Logging;
import ru.clevertec.users.controller.openapi.AuthOpenApi;
import ru.clevertec.users.dto.auth.RegisterDto;
import ru.clevertec.users.dto.auth.TokenDto;
import ru.clevertec.users.dto.auth.LoginDto;
import ru.clevertec.users.dto.auth.AuthDto;
import ru.clevertec.users.dto.users.UserResponse;
import ru.clevertec.users.service.AuthenticationService;

@Logging
@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthController implements AuthOpenApi {

    private final AuthenticationService service;

    @Override
    @PostMapping
    public ResponseEntity<AuthDto> auth(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token) {
        return ResponseEntity.ok(service.authenticate(token));
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody @Valid LoginDto request) {
        return ResponseEntity.ok(service.login(request));
    }

    @Override
    @PostMapping("/register")
    public ResponseEntity<TokenDto> register(@RequestBody @Valid RegisterDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(request));
    }

    @Override
    @PostMapping("/about_me")
    public ResponseEntity<UserResponse> aboutMe(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token) {
        return ResponseEntity.ok(service.aboutMe(token));
    }

}