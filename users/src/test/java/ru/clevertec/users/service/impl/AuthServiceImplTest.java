package ru.clevertec.users.service.impl;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.clevertec.exceptionhandlerstarter.exception.impl.NotFoundElementException;
import ru.clevertec.users.data.builder.impl.dto.auth.AuthDtoBuilder;
import ru.clevertec.users.data.builder.impl.dto.auth.LoginDtoBuilder;
import ru.clevertec.users.data.builder.impl.dto.auth.RegisterDtoBuilder;
import ru.clevertec.users.data.builder.impl.dto.users.UserResBuilder;
import ru.clevertec.users.data.builder.impl.entity.UserBuilder;
import ru.clevertec.users.dto.auth.AuthDto;
import ru.clevertec.users.dto.auth.LoginDto;
import ru.clevertec.users.dto.auth.RegisterDto;
import ru.clevertec.users.dto.auth.TokenDto;
import ru.clevertec.users.dto.users.UserResponse;
import ru.clevertec.users.entity.Role;
import ru.clevertec.users.entity.User;
import ru.clevertec.users.mapper.UserMapper;
import ru.clevertec.users.repository.UserRepository;
import ru.clevertec.users.service.JwtService;

import java.util.Optional;

import static org.mockito.Mockito.verify;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    private final String TOKEN = "Bearer ...";

    @InjectMocks
    private AuthServiceImpl service;

    @Mock
    private UserRepository repository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserMapper mapper;

    @Captor
    private ArgumentCaptor<User> captor;

    @Nested
    class CheckRegister {

        @Test
        void shouldCallRepositoryWithExpectedParameters() {
            RegisterDto request = RegisterDtoBuilder.registerDto().build();
            User user = UserBuilder.user()
                    .withUsername(request.username())
                    .withPassword(null)
                    .withRole(Role.SUBSCRIBER)
                    .withFirstname(request.firstname())
                    .withLastname(request.lastname())
                    .withEmail(request.email())
                    .build();
            doReturn("passwordEncoder").when(passwordEncoder).encode(request.password());
            doReturn(user).when(mapper).toFrom(request);
            service.register(request);
            verify(repository).save(captor.capture());
            User actual = captor.getValue();
            assertAll(
                    () -> assertThat(actual.getUsername()).isEqualTo(request.username()),
                    () -> assertThat(actual.getPassword()).isNotNull(),
                    () -> assertThat(actual.getPassword()).isNotEqualTo(request.password()),
                    () -> assertThat(actual.getRole()).isEqualTo(Role.SUBSCRIBER),
                    () -> assertThat(actual.getFirstname()).isEqualTo(request.firstname()),
                    () -> assertThat(actual.getLastname()).isEqualTo(request.lastname()),
                    () -> assertThat(actual.getEmail()).isEqualTo(request.email()));
        }

        @Test
        void shouldReturnExpected() {
            doReturn(UserBuilder.user().build()).when(mapper).toFrom(any(RegisterDto.class));
            doReturn(TOKEN).when(jwtService).generateToken(any());
            RegisterDto request = RegisterDtoBuilder.registerDto().build();
            TokenDto actual = service.register(request);
            assertThat(actual).isEqualTo(new TokenDto(TOKEN));
        }

    }

    @Nested
    class CheckLogin {

        @Test
        void shouldThrowNotFoundElementException() {
            LoginDto request = LoginDtoBuilder.loginDto().build();
            doReturn(Optional.empty()).when(repository).findByUsername(request.username());
            assertThrows(NotFoundElementException.class, () -> service.login(request));
        }

        @Test
        void shouldReturnExpected() {
            LoginDto request = LoginDtoBuilder.loginDto().withPassword("pass").build();
            User response = UserBuilder.user()
                    .withUsername(request.username())
                    .withPassword(request.password()).build();
            doReturn(Optional.of(response)).when(repository).findByUsername(request.username());
            doReturn(TOKEN).when(jwtService).generateToken(response);
            doReturn(true).when(passwordEncoder).matches(any(), any());
            TokenDto actual = service.login(request);
            assertThat(actual).isEqualTo(new TokenDto(TOKEN));
        }

    }

    @Nested
    class CheckAuthenticate {

        @Test
        void shouldThrowUsernameNotFoundException() {
            doReturn(Optional.empty()).when(repository).findByUsername(any());
            assertThrows(UsernameNotFoundException.class, () -> service.authenticate(TOKEN));
        }

        @Test
        void shouldReturnExpected() {
            User user = UserBuilder.user().build();
            doReturn(user.getUsername()).when(jwtService).extractUsername(any());
            doReturn(Optional.of(user)).when(repository).findByUsername(user.getUsername());
            AuthDto response = AuthDtoBuilder.authDto()
                    .withUsername(user.getUsername())
                    .withAuthorities(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                    .build();
            AuthDto actual = service.authenticate(TOKEN);
            assertThat(actual).isEqualTo(response);
        }

    }

    @Nested
    class CheckAboutMe {

        @Test
        void shouldThrowUsernameNotFoundException() {
            doReturn(Optional.empty()).when(repository).findByUsername(any());
            assertThrows(UsernameNotFoundException.class, () -> service.aboutMe(TOKEN));
        }

        @Test
        void shouldReturnExpected() {
            User user = UserBuilder.user().build();
            doReturn(user.getUsername()).when(jwtService).extractUsername(any());
            doReturn(Optional.of(user)).when(repository).findByUsername(user.getUsername());
            UserResponse response = UserResBuilder.userRes()
                    .withUsername(user.getUsername())
                    .withRoles(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                    .withFirstname(user.getFirstname())
                    .withLastname(user.getLastname())
                    .withEmail(user.getEmail())
                    .build();
            doReturn(response).when(mapper).toFrom(user);
            service.aboutMe(TOKEN);
        }

    }

}