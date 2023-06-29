package ru.clevertec.users.service;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.clevertec.users.dto.users.UserFilter;
import ru.clevertec.users.dto.users.UserRequest;
import ru.clevertec.users.dto.users.UserResponse;

import java.util.List;

public interface UserService extends UserDetailsService {

    List<UserResponse> findAll(UserFilter filter, Pageable pageable);

    UserResponse findById(Long id);

    UserResponse update(Long id, UserRequest request);

    void delete(Long id);

}