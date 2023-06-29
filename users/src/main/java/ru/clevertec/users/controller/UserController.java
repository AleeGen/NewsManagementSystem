package ru.clevertec.users.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.loggingstarter.annotation.Logging;
import ru.clevertec.users.controller.openapi.UserOpenApi;
import ru.clevertec.users.dto.users.UserFilter;
import ru.clevertec.users.dto.users.UserRequest;
import ru.clevertec.users.dto.users.UserResponse;
import ru.clevertec.users.service.UserService;

import java.util.List;

@Logging
@RestController
@RequiredArgsConstructor
@RequestMapping("users")
public class UserController implements UserOpenApi {

    private final UserService service;

    @Override
    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll(UserFilter filter, Pageable pageable) {
        return new ResponseEntity<>(service.findAll(filter, pageable), HttpStatus.OK);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> get(@PathVariable Long id) {
        return new ResponseEntity<>(service.findById(id), HttpStatus.OK);
    }

    @Override
    @PutMapping("/{id}")
    @PreAuthorize("@userServiceImpl.findById(#id).username().equals(principal.username)")
    public ResponseEntity<UserResponse> put(@PathVariable Long id,
                                            @RequestBody @Valid UserRequest request) {
        return new ResponseEntity<>(service.update(id, request), HttpStatus.CREATED);
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') || @userServiceImpl.findById(#id).username().equals(principal.username)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}