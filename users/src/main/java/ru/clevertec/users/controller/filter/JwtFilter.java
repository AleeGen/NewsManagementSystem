package ru.clevertec.users.controller.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.clevertec.users.service.JwtService;
import ru.clevertec.users.service.UserService;

import java.io.IOException;

/**
 * A class that provides a filter for checking the JWT token on each request.
 * * Implements the {@link OncePerRequestFilter} interface  to provide a single filter execution for each request.
 * Uses {@link UserService} and {@link JwtService} services to verify the validity of the token and user authentication.
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final JwtService jwtService;

    /**
     * Method for checking the JWT token at each request.
     * If the token is missing or does not start with the prefix "Bearer ", the request is passed on without changes.
     * If the token is valid and the user is authenticated, an authentication object is created and stored in the security context.
     *
     * @param request     an object of the {@link HttpServletRequest} class containing information about the request.
     * @param response    an object of the {@link HttpServletResponse} class containing information about the response.
     * @param filterChain an object of the {@link FilterChain} class representing a chain of filters for processing a request.
     * @throws ServletException if an error occurred while processing the request.
     * @throws IOException      if an I/O error occurred while processing the request.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.loadUserByUsername(username);
            if (jwtService.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

}