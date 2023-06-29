package ru.clevertec.users.service;

import ru.clevertec.users.dto.auth.AuthDto;
import ru.clevertec.users.dto.auth.LoginDto;
import ru.clevertec.users.dto.auth.RegisterDto;
import ru.clevertec.users.dto.auth.TokenDto;
import ru.clevertec.users.dto.users.UserResponse;

public interface AuthenticationService {

    TokenDto register(RegisterDto request);

    TokenDto login(LoginDto request);

    AuthDto authenticate(String token);

    UserResponse aboutMe(String token);

}