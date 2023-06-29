package ru.clevertec.users.integration.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.clevertec.exceptionhandlerstarter.exception.impl.NotFoundElementException;
import ru.clevertec.users.data.builder.impl.dto.users.UserFilterBuilder;
import ru.clevertec.users.data.builder.impl.dto.users.UserReqBuilder;
import ru.clevertec.users.dto.users.UserFilter;
import ru.clevertec.users.dto.users.UserRequest;
import ru.clevertec.users.dto.users.UserResponse;
import ru.clevertec.users.integration.AbstractTestContainer;
import ru.clevertec.users.service.impl.UserServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.clevertec.users.data.reader.UserReader.findAllUserResponseDefault;
import static ru.clevertec.users.data.reader.UserReader.findAllWithFilter;
import static ru.clevertec.users.data.reader.UserReader.findAllWithParametersPageable;
import static ru.clevertec.users.data.reader.UserReader.findById;

@RequiredArgsConstructor
public class UserServiceIT extends AbstractTestContainer {

    private final UserServiceImpl service;

    @Nested
    class CheckFindAll {

        @Test
        @SneakyThrows
        void checkDefaultPageableShouldReturnExpected() {
            UserFilter filter = UserFilterBuilder.userFilter().build();
            var expected = findAllUserResponseDefault();
            var actual = service.findAll(filter, PageRequest.of(0, 20));
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @SneakyThrows
        void checkWithParametersPageableShouldReturnExpected() {
            UserFilter filter = UserFilterBuilder.userFilter().build();
            Pageable pageable = Pageable.ofSize(2).withPage(1);
            var expected = findAllWithParametersPageable();
            var actual = service.findAll(filter, pageable);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @SneakyThrows
        void checkWithFilterShouldReturnExpected() {
            UserFilter filter = UserFilterBuilder.userFilter().withFirstname("maksim").build();
            var expected = findAllWithFilter();
            var actual = service.findAll(filter, PageRequest.of(0, 20));
            assertThat(actual).isEqualTo(expected);
        }

    }

    @Nested
    class CheckFindById {

        @Test
        void shouldThrowNotFoundElementException() {
            long nonExistId = -1L;
            assertThrows(NotFoundElementException.class, () -> service.findById(nonExistId));
        }

        @SneakyThrows
        @ParameterizedTest
        @ValueSource(longs = {1, 2, 3})
        void defaultPageableShouldReturnExpected(Long id) {
            UserResponse expected = findById(id);
            UserResponse actual = service.findById(id);
            assertThat(actual).isEqualTo(expected);
        }

    }


    @Nested
    class CheckUpdate {

        @Test
        void shouldThrowNotFoundElementException() {
            long nonExistId = -1L;
            UserRequest request = UserReqBuilder.userReq().build();
            assertThrows(NotFoundElementException.class, () -> service.update(nonExistId, request));
        }

        @Test
        void shouldUpdate() {
            UserRequest request = UserReqBuilder.userReq().build();
            UserResponse actual = service.update(1L, request);
            assertAll(
                    () -> assertThat(actual.firstname()).isEqualTo(request.firstname()),
                    () -> assertThat(actual.lastname()).isEqualTo(request.lastname()),
                    () -> assertThat(actual.email()).isEqualTo(request.email()));
        }

    }


    @Nested
    class CheckDelete {

        @Test
        void shouldThrowNotFoundElementException() {
            long nonExistId = -1L;
            assertThrows(NotFoundElementException.class, () -> service.delete(nonExistId));
        }

        @Test
        void shouldDelete() {
            assertDoesNotThrow(() -> service.delete(1L));
        }

    }

    @Nested
    class CheckLoadByUsername {

        @Test
        void shouldThrowUsernameNotFoundException() {
            String nonExistUsername = "nonExistUsername";
            assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername(nonExistUsername));
        }

        @ParameterizedTest
        @ValueSource(strings = {"leon", "maks", "dima"})
        void shouldReturnExpected(String username) {
            UserDetails actual = service.loadUserByUsername(username);
            assertThat(actual.getUsername()).isEqualTo(username);
        }

    }

}