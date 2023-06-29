package ru.clevertec.news.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.loggingstarter.annotation.Logging;
import ru.clevertec.news.client.ExternalUserApi;
import ru.clevertec.news.controller.openapi.UserOpenApi;
import ru.clevertec.news.dto.users.UserFilter;
import ru.clevertec.news.dto.users.UserRequest;
import ru.clevertec.news.dto.users.UserResponse;

import java.util.List;

@Logging
@RestController
@RequiredArgsConstructor
@RequestMapping("users")
public class UserController implements UserOpenApi {

    private final ExternalUserApi externalUserApi;

    @Override
    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token,
                                                      UserFilter filter,
                                                      Pageable pageable) {
        return externalUserApi.getAll(token, filter, pageable);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> get(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                            @PathVariable Long id) {
        return externalUserApi.get(token, id);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> put(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                            @PathVariable Long id,
                                            @RequestBody @Valid UserRequest request) {
        return externalUserApi.put(token, id, request);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                       @PathVariable Long id) {
        return externalUserApi.delete(token, id);
    }

}