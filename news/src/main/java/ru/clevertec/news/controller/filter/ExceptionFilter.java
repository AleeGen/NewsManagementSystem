package ru.clevertec.news.controller.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.clevertec.exceptionhandlerstarter.handler.response.impl.ExceptionResponse;

import java.io.IOException;

/**
 * A filter that catches exceptions thrown during request processing and returns a JSON response with error details.
 */
@Component
@RequiredArgsConstructor
public class ExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper mapper;

    /**
     * Processes the request and response by invoking the next filter in the chain,
     * and catches any exceptions that are thrown.
     * If an exception is caught, a JSON response with error details is returned.
     *
     * @param request     the HTTP servlet request
     * @param response    the HTTP servlet response
     * @param filterChain the filter chain
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Throwable e) {
            int status = HttpStatus.FORBIDDEN.value();
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(status);
            ExceptionResponse exceptionResponse = new ExceptionResponse();
            exceptionResponse.setStatus(status);
            exceptionResponse.setMessage(e.getMessage());
            response.getWriter().write(mapper.writeValueAsString(exceptionResponse));
        }
    }

}