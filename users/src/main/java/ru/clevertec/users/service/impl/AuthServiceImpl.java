package ru.clevertec.users.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.clevertec.exceptionhandlerstarter.exception.AbstractException;
import ru.clevertec.exceptionhandlerstarter.exception.impl.NotFoundElementException;
import ru.clevertec.users.dto.auth.RegisterDto;
import ru.clevertec.users.dto.auth.TokenDto;
import ru.clevertec.users.dto.auth.LoginDto;
import ru.clevertec.users.dto.auth.AuthDto;
import ru.clevertec.users.dto.users.UserResponse;
import ru.clevertec.users.entity.User;
import ru.clevertec.users.mapper.UserMapper;
import ru.clevertec.users.repository.UserRepository;
import ru.clevertec.users.service.AuthenticationService;
import ru.clevertec.users.service.JwtService;

import java.util.List;

/**
 * A class that provides methods for user authentication and authorization.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper mapper;
    private final String NOT_FOUND = "User with username = %s not found";

    /**
     * Method for registering a new user.
     * Creates a new user with the specified data, saves it to the database and generates a JWT token for it.
     *
     * @param request an object of the RegisterDto class containing the data of the new user.
     * @return {@link TokenDto} class object containing a JWT token for a new user.
     */
    @Override
    public TokenDto register(RegisterDto request) {
        User user = mapper.toFrom(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        String jwtToken = jwtService.generateToken(repository.save(user));
        return new TokenDto(jwtToken);
    }

    /**
     * Method for authenticating a user using his credentials.
     * Checks for the presence of a user with the specified name in the database, generates a JWT token for him.
     *
     * @param request an object of the LoginDto class containing the username and password.
     * @return {@link TokenDto} class object containing a JWT token for the user.
     * @throws UsernameNotFoundException if the user with the specified name is not found in the database.
     * @throws AbstractException         if passwords not matches.
     */
    @Override
    public TokenDto login(LoginDto request) {
        User user = repository.findByUsername(request.username()).orElseThrow(() -> new NotFoundElementException(
                String.format(NOT_FOUND, request.username()), HttpStatus.NOT_FOUND.value()));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new AbstractException("Invalid password", HttpStatus.FORBIDDEN.value());
        }
        String jwtToken = jwtService.generateToken(user);
        return new TokenDto(jwtToken);
    }

    /**
     * Method for checking the JWT token and getting information about the authenticated user.
     * Extracts the username from the JWT token, finds it in the database and returns information about it.
     *
     * @param token a string containing a JWT token.
     * @return an object of the {@link AuthDto} containing the user's name and a list of his roles.
     * @throws UsernameNotFoundException if the user with the specified name is not found in the database.
     */
    @Override
    public AuthDto authenticate(String token) {
        String username = jwtService.extractUsername(token.substring(7));
        User user = repository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException(String.format(NOT_FOUND, username)));
        List<String> list = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return new AuthDto(user.getUsername(), list);
    }

    /**
     * Method for getting information about the current user.
     * Extracts the username from the JWT token, finds it in the database and returns information about it.
     *
     * @param token a string containing a JWT token.
     * @return an object of the {@link UserResponse} containing information about the current user.
     * @throws UsernameNotFoundException if the user with the specified name is not found in the database.
     */
    @Override
    public UserResponse aboutMe(String token) {
        String username = jwtService.extractUsername(token.substring(7));
        User user = repository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException(String.format(NOT_FOUND, username)));
        return mapper.toFrom(user);
    }

}