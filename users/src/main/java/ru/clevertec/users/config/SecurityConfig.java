package ru.clevertec.users.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.clevertec.exceptionhandlerstarter.handler.response.AbstractErrorResponse;
import ru.clevertec.exceptionhandlerstarter.handler.response.impl.CommunicationResponse;
import ru.clevertec.users.controller.filter.ExceptionFilter;
import ru.clevertec.users.controller.filter.JwtFilter;
import ru.clevertec.users.entity.Role;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper mapper;
    private final JwtFilter jwtFilter;
    private final ExceptionFilter exceptionFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        .requestMatchers("/auth/register", "/auth/login").anonymous()
                        .requestMatchers("/auth/about_me", "/auth").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/users/{id}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/users/{id}").authenticated()
                        .anyRequest().hasAuthority(Role.ADMIN.getAuthority()))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionFilter, JwtFilter.class)
                .exceptionHandling(this::exceptionHandle)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private void exceptionHandle(ExceptionHandlingConfigurer<HttpSecurity> handling) {
        handling.authenticationEntryPoint(this::form);
        handling.accessDeniedHandler(this::form);
    }

    private void form(HttpServletRequest req, HttpServletResponse resp, Exception exception) throws IOException {
        int status = HttpStatus.FORBIDDEN.value();
        AbstractErrorResponse errorResponse = new CommunicationResponse();
        errorResponse.setMessage(String.format("URI: %s. Method: %s. Error: %s",
                req.getRequestURI(),
                req.getMethod(),
                exception.getMessage()));
        errorResponse.setStatus(status);
        resp.setStatus(status);
        resp.setContentType(APPLICATION_JSON_VALUE);
        resp.getWriter().write(mapper.writeValueAsString(errorResponse));
    }

}