package ru.clevertec.news.config;

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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.clevertec.exceptionhandlerstarter.handler.response.AbstractErrorResponse;
import ru.clevertec.exceptionhandlerstarter.handler.response.impl.CommunicationResponse;
import ru.clevertec.news.controller.filter.ExceptionFilter;
import ru.clevertec.news.controller.filter.JwtFilter;
import ru.clevertec.news.entity.Role;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static ru.clevertec.news.entity.Role.JOURNALIST;
import static ru.clevertec.news.entity.Role.SUBSCRIBER;

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
                        .requestMatchers(HttpMethod.GET).permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/auth/**", "/users/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/comments/**").hasAuthority(SUBSCRIBER.name())
                        .requestMatchers(HttpMethod.PUT, "/comments/**").hasAuthority(SUBSCRIBER.name())
                        .requestMatchers(HttpMethod.PATCH, "/comments/**").hasAuthority(SUBSCRIBER.name())
                        .requestMatchers(HttpMethod.DELETE, "/comments/**").hasAuthority(SUBSCRIBER.name())
                        .requestMatchers(HttpMethod.POST, "/news/**").hasAuthority(JOURNALIST.name())
                        .requestMatchers(HttpMethod.PUT, "/news/**").hasAuthority(JOURNALIST.name())
                        .requestMatchers(HttpMethod.PATCH, "/news/**").hasAuthority(JOURNALIST.name())
                        .requestMatchers(HttpMethod.DELETE, "/news/**").hasAuthority(JOURNALIST.name())
                        .anyRequest().hasAuthority(Role.ADMIN.name()))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionFilter, JwtFilter.class)
                .exceptionHandling(this::exceptionHandle)
                .build();
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