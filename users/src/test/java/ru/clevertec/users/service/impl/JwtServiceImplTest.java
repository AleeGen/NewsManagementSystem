package ru.clevertec.users.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.clevertec.users.data.reader.TokenReader.invalidBySyntax;
import static ru.clevertec.users.data.reader.TokenReader.invalidByTime;
import static ru.clevertec.users.data.reader.TokenReader.valid;

@SpringBootTest(classes = JwtServiceImpl.class)
class JwtServiceImplTest {

    @Autowired
    private JwtServiceImpl service;

    @Nested
    class CheckExtractUsername {

        @Test
        void shouldThrowMalformedJwtException() {
            assertThrows(MalformedJwtException.class, () -> service.extractUsername(invalidBySyntax()));
        }

        @Test
        void shouldThrowExpiredJwtException() {
            assertThrows(ExpiredJwtException.class, () -> service.extractUsername(invalidByTime()));
        }

        @Test
        void shouldReturnExpected() {
            String expected = "leon";
            String actual = service.extractUsername(valid());
            assertThat(actual).isEqualTo(expected);
        }

    }

    @Nested
    class CheckExtractClaim {

        private static Stream<Function<Claims, Object>> function() {
            return Stream.of(
                    Claims::getExpiration,
                    Claims::getSubject,
                    Claims::getIssuedAt);
        }

        @Test
        void shouldThrowMalformedJwtException() {
            assertThrows(MalformedJwtException.class,
                    () -> service.extractClaim(invalidBySyntax(), Claims::getExpiration));
        }

        @Test
        void shouldThrowExpiredJwtException() {
            assertThrows(ExpiredJwtException.class,
                    () -> service.extractClaim(invalidByTime(), Claims::getExpiration));
        }

        @ParameterizedTest
        @MethodSource("function")
        void shouldReturnExpected(Function<Claims, Object> claimsResolver) {
            Object actual = service.extractClaim(valid(), claimsResolver);
            assertThat(actual).isNotNull();
        }

    }

    @Nested
    class CheckGenerateToken {

        @Test
        void generateToken() {
            UserDetails userDetails = User.builder().username("username").password("").build();
            assertDoesNotThrow(() -> service.generateToken(userDetails));
        }

    }

    @Nested
    class CheckIsTokenValid {

        private static Stream<Arguments> negative() {
            return Stream.of(
                    Arguments.of(invalidByTime(), "leon"),
                    Arguments.of(invalidBySyntax(), "leon"),
                    Arguments.of(valid(), "ivan"));
        }

        @Test
        void shouldReturnTrue() {
            UserDetails userDetails = User.builder().username("leon").password("").build();
            assertTrue(service.isTokenValid(valid(), userDetails));
        }

        @ParameterizedTest
        @MethodSource("negative")
        void shouldReturnFalse(String token, String username) {
            UserDetails userDetails = User.builder().username(username).password("").build();
            assertFalse(service.isTokenValid(token, userDetails));
        }

    }

}