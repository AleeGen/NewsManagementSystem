package ru.clevertec.users.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.cachestarter.annotation.CacheMethod;
import ru.clevertec.cachestarter.annotation.Caching;
import ru.clevertec.users.dto.users.UserFilter;
import ru.clevertec.users.dto.users.UserRequest;
import ru.clevertec.users.dto.users.UserResponse;
import ru.clevertec.users.entity.User;
import ru.clevertec.exceptionhandlerstarter.exception.impl.NotFoundElementException;
import ru.clevertec.users.mapper.UserMapper;
import ru.clevertec.users.repository.UserRepository;
import ru.clevertec.users.service.UserService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository rep;
    private final UserMapper mapper;
    private final String NOT_FOUND = "User with id = %d not found";

    @Override
    @Caching(key = "username", method = CacheMethod.EXTRACT)
    public List<UserResponse> findAll(UserFilter filter, Pageable pageable) {
        return rep.findAll(Example.of(mapper.toFrom(filter)), pageable)
                .map(mapper::toFrom)
                .stream().toList();
    }

    @Override
    @Caching(key = "id")
    public UserResponse findById(Long id) {
        User user = rep.findById(id).orElseThrow(() ->
                new NotFoundElementException(String.format(NOT_FOUND, id), HttpStatus.NOT_FOUND.value()));
        return mapper.toFrom(user);
    }

    @Override
    @Transactional
    @Caching(key = "id", method = CacheMethod.CHANGE)
    public UserResponse update(Long id, UserRequest request) {
        User user = rep.findById(id).orElseThrow(() ->
                new NotFoundElementException(String.format(NOT_FOUND, id), HttpStatus.NOT_FOUND.value()));
        mapper.update(user, request);
        return mapper.toFrom(user);
    }

    @Override
    @Transactional
    @Caching(key = "id", method = CacheMethod.DELETE)
    public void delete(Long id) {
        Optional<User> user = rep.findById(id);
        rep.delete(user.orElseThrow(() ->
                new NotFoundElementException(String.format(NOT_FOUND, id), HttpStatus.NOT_FOUND.value())));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return rep.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Failed to retrieve user: " + username));
    }

}