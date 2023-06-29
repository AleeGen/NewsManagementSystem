package ru.clevertec.users.service.impl;

import java.util.Optional;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.clevertec.exceptionhandlerstarter.exception.impl.NotFoundElementException;
import ru.clevertec.users.data.builder.impl.dto.users.UserFilterBuilder;
import ru.clevertec.users.data.builder.impl.dto.users.UserReqBuilder;
import ru.clevertec.users.data.builder.impl.dto.users.UserResBuilder;
import ru.clevertec.users.data.builder.impl.entity.UserBuilder;
import ru.clevertec.users.dto.users.UserFilter;
import ru.clevertec.users.dto.users.UserRequest;
import ru.clevertec.users.dto.users.UserResponse;
import ru.clevertec.users.entity.User;
import ru.clevertec.users.mapper.UserMapper;
import ru.clevertec.users.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static ru.clevertec.users.data.reader.UserReader.findAllUserDefault;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl service;

    @Mock
    private UserRepository rep;

    @Mock
    private UserMapper mapper;

    @Nested
    class CheckFindAll {

        @Test
        @SneakyThrows
        void shouldReturnExpected() {
            int expected = 3;
            Page<User> users = new PageImpl<>(findAllUserDefault());
            UserFilter filter = UserFilterBuilder.userFilter().build();
            doReturn(UserBuilder.user().build()).when(mapper).toFrom(filter);
            doReturn(users).when(rep).findAll(any(Example.class), any(Pageable.class));
            int actual = service.findAll(filter, Pageable.unpaged()).size();
            assertThat(actual).isEqualTo(expected);
        }

    }

    @Nested
    class CheckFindById {

        @Test
        void shouldThrowNotFoundElementException() {
            long nonExistId = -1L;
            doReturn(Optional.empty()).when(rep).findById(nonExistId);
            assertThrows(NotFoundElementException.class, () -> service.findById(nonExistId));
        }

        @Test
        void shouldReturnExpected() {
            long id = 1L;
            User user = UserBuilder.user().withId(id).build();
            UserResponse expected = UserResBuilder.userRes().build();
            doReturn(Optional.of(user)).when(rep).findById(id);
            doReturn(expected).when(mapper).toFrom(user);
            UserResponse actual = service.findById(id);
            assertThat(actual).isEqualTo(expected);
        }

    }

    @Nested
    class CheckUpdate {

        @Test
        void shouldThrowNotFoundElementException() {
            long nonExistId = -1L;
            doReturn(Optional.empty()).when(rep).findById(nonExistId);
            assertThrows(NotFoundElementException.class, () -> service.update(nonExistId, any()));
        }

        @Test
        void shouldCallMapperUpdate() {
            long id = 1L;
            UserRequest request = UserReqBuilder.userReq().build();
            User user = UserBuilder.user().build();
            doReturn(Optional.of(user)).when(rep).findById(id);
            service.update(id, request);
            verify(mapper).update(user, request);
        }

        @Test
        void shouldReturnExpected() {
            long id = 1L;
            UserRequest request = UserReqBuilder.userReq().build();
            User user = UserBuilder.user().build();
            UserResponse expected = UserResponse.builder().build();
            doReturn(Optional.of(user)).when(rep).findById(id);
            doReturn(expected).when(mapper).toFrom(user);
            UserResponse actual = service.update(id, request);
            assertThat(actual).isEqualTo(expected);
        }

    }

    @Nested
    class CheckDelete {

        @Test
        void shouldThrowNotFoundElementException() {
            long nonExistId = -1L;
            doReturn(Optional.empty()).when(rep).findById(nonExistId);
            assertThrows(NotFoundElementException.class, () -> service.delete(nonExistId));
        }

        @Test
        void shouldCallDelete() {
            long id = -1L;
            User user = UserBuilder.user().withId(id).build();
            doReturn(Optional.of(user)).when(rep).findById(id);
            assertAll(() -> assertDoesNotThrow(() -> service.delete(id)),
                    () -> verify(rep).delete(user));
        }

    }

    @Nested
    class CheckLoadUserByUsername {

        @Test
        void shouldThrowUsernameNotFoundException() {
            doReturn(Optional.empty()).when(rep).findByUsername(any());
            assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername(any()));
        }

        @Test
        void shouldReturnExpected() {
            User expected = UserBuilder.user().build();
            doReturn(Optional.of(expected)).when(rep).findByUsername(anyString());
            UserDetails actual = service.loadUserByUsername(anyString());
            assertThat(actual).isEqualTo(expected);
        }

    }

}