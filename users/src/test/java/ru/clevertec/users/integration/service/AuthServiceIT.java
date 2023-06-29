package ru.clevertec.users.integration.service;

import java.util.List;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.clevertec.exceptionhandlerstarter.exception.AbstractException;
import ru.clevertec.exceptionhandlerstarter.exception.impl.NotFoundElementException;
import ru.clevertec.users.data.builder.impl.dto.auth.AuthDtoBuilder;
import ru.clevertec.users.data.builder.impl.dto.auth.LoginDtoBuilder;
import ru.clevertec.users.data.builder.impl.dto.auth.RegisterDtoBuilder;
import ru.clevertec.users.dto.auth.AuthDto;
import ru.clevertec.users.dto.auth.LoginDto;
import ru.clevertec.users.dto.auth.RegisterDto;
import ru.clevertec.users.dto.auth.TokenDto;
import ru.clevertec.users.dto.users.UserResponse;
import ru.clevertec.users.integration.AbstractTestContainer;
import ru.clevertec.users.service.impl.AuthServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.clevertec.users.data.reader.TokenReader.describersTokenWithPrefix;
import static ru.clevertec.users.data.reader.TokenReader.journalistsTokenWithPrefix;
import static ru.clevertec.users.data.reader.TokenReader.nonExistUserWithPrefix;
import static ru.clevertec.users.data.reader.TokenReader.validWithPrefix;
import static ru.clevertec.users.data.reader.UserReader.findById;
import static ru.clevertec.users.entity.Role.ADMIN;
import static ru.clevertec.users.entity.Role.JOURNALIST;
import static ru.clevertec.users.entity.Role.SUBSCRIBER;

@RequiredArgsConstructor
public class AuthServiceIT extends AbstractTestContainer {

    private final AuthServiceImpl service;

    @Nested
    class CheckRegister {

        @Test
        void shouldReturnExpected() {
            RegisterDto registerDto = RegisterDtoBuilder.registerDto().build();
            TokenDto actual = service.register(registerDto);
            assertNotNull(actual);
        }

    }

    @Nested
    class CheckLogin {

        @Test
        void shouldThrowNotFoundElementException() {
            LoginDto loginDto = LoginDtoBuilder.loginDto().withUsername("nonExistUsername").build();
            assertThrows(NotFoundElementException.class, () -> service.login(loginDto));
        }

        @Test
        void shouldThrowAbstractException() {
            LoginDto loginDto = LoginDtoBuilder.loginDto()
                    .withUsername("leon")
                    .withPassword("incorrectPassword").build();
            assertThrows(AbstractException.class, () -> service.login(loginDto));
        }

        @Test
        void shouldReturnExpected() {
            LoginDto loginDto = LoginDtoBuilder.loginDto()
                    .withUsername("leon")
                    .withPassword("pass").build();
            TokenDto actual = service.login(loginDto);
            assertNotNull(actual);
        }

    }

    @Nested
    class CheckAuthenticate {

        private static Stream<Arguments> tokenAndAuthDto() {
            return Stream.of(
                    Arguments.of(validWithPrefix(), AuthDtoBuilder.authDto()
                            .withUsername("leon").withAuthorities(List.of(ADMIN.name())).build()),
                    Arguments.of(journalistsTokenWithPrefix(), AuthDtoBuilder.authDto()
                            .withUsername("maks").withAuthorities(List.of(JOURNALIST.name())).build()),
                    Arguments.of(describersTokenWithPrefix(), AuthDtoBuilder.authDto()
                            .withUsername("dima").withAuthorities(List.of(SUBSCRIBER.name())).build()));
        }

        @ParameterizedTest
        @MethodSource("tokenAndAuthDto")
        void shouldReturnExpected(String token, AuthDto expected) {
            AuthDto actual = service.authenticate(token);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void shouldThrowUsernameNotFoundException() {
            assertThrows(UsernameNotFoundException.class, () -> service.authenticate(nonExistUserWithPrefix()));
        }

    }

    @Nested
    class CheckAboutMe {

        @SneakyThrows
        private static Stream<Arguments> tokenAndUserResponse() {
            return Stream.of(
                    Arguments.of(validWithPrefix(), findById(1L)),
                    Arguments.of(journalistsTokenWithPrefix(), findById(2L)),
                    Arguments.of(describersTokenWithPrefix(), findById(3L)));
        }

        @ParameterizedTest
        @MethodSource("tokenAndUserResponse")
        void shouldReturnExpected(String token, UserResponse expected) {
            UserResponse actual = service.aboutMe(token);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void shouldThrowUsernameNotFoundException() {
            assertThrows(UsernameNotFoundException.class, () -> service.aboutMe(nonExistUserWithPrefix()));
        }

    }

}