package ru.clevertec.news.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestParam;
import ru.clevertec.news.dto.auth.TokenDto;
import ru.clevertec.news.dto.auth.LoginDto;
import ru.clevertec.news.dto.auth.RegisterDto;
import ru.clevertec.news.dto.auth.AuthDto;
import ru.clevertec.news.dto.users.UserFilter;
import ru.clevertec.news.dto.users.UserRequest;
import ru.clevertec.news.dto.users.UserResponse;

import java.util.List;

@FeignClient(name = "users", url = "${client.users-service.url}")
public interface ExternalUserApi {

    @PostMapping("/auth")
    ResponseEntity<AuthDto> getUserDetails(@RequestHeader(HttpHeaders.AUTHORIZATION) String token);

    @PostMapping("/auth/login")
    ResponseEntity<TokenDto> login(@RequestBody LoginDto request);

    @PostMapping("/auth/register")
    ResponseEntity<TokenDto> register(@RequestBody RegisterDto request);

    @PostMapping("/auth/about_me")
    ResponseEntity<UserResponse> aboutMe(@RequestHeader(HttpHeaders.AUTHORIZATION) String token);

    @GetMapping("/users")
    ResponseEntity<List<UserResponse>> getAll(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                              @RequestParam UserFilter filter,
                                              Pageable pageable);

    @GetMapping("users/{id}")
    ResponseEntity<UserResponse> get(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                     @PathVariable Long id);

    @PutMapping("users/{id}")
    ResponseEntity<UserResponse> put(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                     @PathVariable Long id,
                                     @RequestBody UserRequest request);

    @DeleteMapping("users/{id}")
    ResponseEntity<Void> delete(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                @PathVariable Long id);

}