package ru.clevertec.news.controller.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.clevertec.exceptionhandlerstarter.exception.impl.CommunicationException;
import ru.clevertec.news.client.ExternalUserApi;
import ru.clevertec.news.dto.auth.AuthDto;

import java.io.IOException;
import java.util.Objects;

/**
 * Filter for checking the authorization token and getting user details from the external API.
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    /**
     * External API for getting user details
     */
    private final ExternalUserApi externalUserApi;

    /**
     * Checks the authorization token and gets the user details from the external API.
     * If the token is missing or does not start with "Bearer", then the request is passed on without changes.
     * If the token is incorrect or there was a communication error when receiving user details,
     * then an exception to the type CommunicationException is thrown.
     *
     * @param request     HTTP request
     * @param response    HTTP response
     * @param filterChain filter chain
     * @throws ServletException       in case of servlet error
     * @throws IOException            in case of an error reading data from the server response
     * @throws CommunicationException in case of communication error with external API
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token == null || !token.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        AuthDto authDto = externalUserApi.getUserDetails(token).getBody();
        UserDetails userDetails = User.builder()
                .password("")
                .username(Objects.requireNonNull(authDto).username())
                .authorities(authDto.authorities().toArray(String[]::new))
                .build();
        UsernamePasswordAuthenticationToken authenticated = UsernamePasswordAuthenticationToken
                .authenticated(userDetails, null, userDetails.getAuthorities());
        authenticated.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticated);
        filterChain.doFilter(request, response);
    }

}