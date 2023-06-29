package ru.clevertec.news.integration.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.stream.Stream;

import ru.clevertec.exceptionhandlerstarter.exception.impl.BanException;
import ru.clevertec.exceptionhandlerstarter.exception.impl.NotFoundElementException;
import ru.clevertec.exceptionhandlerstarter.exception.impl.PatchException;
import ru.clevertec.news.data.builder.impl.dto.news.NewsReqBuilder;
import ru.clevertec.news.data.builder.impl.dto.patch.PatchReqBuilder;
import ru.clevertec.news.dto.news.NewsFilter;
import ru.clevertec.news.integration.AbstractTestContainer;
import ru.clevertec.news.service.impl.NewsServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.clevertec.news.data.reader.NewsReader.findAllDefaultPageable;
import static ru.clevertec.news.data.reader.NewsReader.findAllWithFilter;
import static ru.clevertec.news.data.reader.NewsReader.findAllWithParametersPageable;
import static ru.clevertec.news.data.reader.NewsReader.findByIdDefaultPageable;
import static ru.clevertec.news.data.reader.NewsReader.findByIdWithParametersPageable;

@RequiredArgsConstructor
public class NewsServiceIT extends AbstractTestContainer {

    private final NewsServiceImpl service;
    private final Pageable defaultPageable = PageRequest.of(0, 20);

    @Nested
    class CheckFindAll {

        @Test
        @SneakyThrows
        void checkDefaultPageableShouldReturnExpected() {
            var expected = findAllDefaultPageable();
            var filter = NewsFilter.builder().build();
            var actual = service.findAll(filter, defaultPageable);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @SneakyThrows
        void checkWithParametersPageableShouldReturnExpected() {
            var expected = findAllWithParametersPageable();
            var filter = NewsFilter.builder().build();
            var pageable = PageRequest.of(2, 5);
            var actual = service.findAll(filter, pageable);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @SneakyThrows
        void checkWithFilterShouldReturnExpected() {
            var expected = findAllWithFilter();
            var filter = NewsFilter.builder().title("Title-3").build();
            var actual = service.findAll(filter, defaultPageable);
            assertThat(actual).isEqualTo(expected);
        }

    }

    @Nested
    class CheckFindById {

        private static Stream<Long> ids() {
            return Stream.of(1L, 8L, 13L, 20L);
        }

        @Test
        void shouldThrowNotFoundElementException() {
            long nonExistId = -1L;
            assertThrows(NotFoundElementException.class, () -> service.findById(nonExistId, defaultPageable));
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("ids")
        void defaultPageableShouldReturnExpected(Long id) {
            var expected = findByIdDefaultPageable(id);
            var actual = service.findById(id, defaultPageable);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @SneakyThrows
        void withParametersPageableShouldReturnExpected() {
            var expected = findByIdWithParametersPageable();
            var pageable = PageRequest.of(3, 2);
            var actual = service.findById(1L, pageable);
            assertThat(actual).isEqualTo(expected);
        }

    }

    @Nested
    class CheckSave {

        @Test
        void shouldSave() {
            var request = NewsReqBuilder.newsReq()
                    .withUsername("newUsername")
                    .withTitle("newTitle")
                    .withText("newText").build();
            var actual = service.save(request);
            assertAll(
                    () -> assertThat(actual.id()).isNotNull(),
                    () -> assertThat(actual.username()).isEqualTo(request.username()),
                    () -> assertThat(actual.time()).isNotNull(),
                    () -> assertThat(actual.title()).isEqualTo(request.title()),
                    () -> assertThat(actual.text()).isEqualTo(request.text()));
        }

    }

    @Nested
    class CheckUpdate {

        @Test
        void shouldThrowNotFoundElementException() {
            long nonExistId = -1L;
            var request = NewsReqBuilder.newsReq().build();
            assertThrows(NotFoundElementException.class, () -> service.update(nonExistId, request, defaultPageable));
        }

        @Test
        void shouldUpdate() {
            long id = 1L;
            var request = NewsReqBuilder.newsReq()
                    .withTitle("newTitle")
                    .withText("newText").build();
            var actual = service.update(id, request, defaultPageable);
            assertAll(
                    () -> assertThat(actual.id()).isEqualTo(id),
                    () -> assertThat(actual.time()).isNotNull(),
                    () -> assertThat(actual.title()).isEqualTo(request.title()),
                    () -> assertThat(actual.text()).isEqualTo(request.text()));
        }

    }

    @Nested
    class CheckPatch {

        private static Stream<Arguments> negativePatchRequest() {
            return Stream.of(
                    Arguments.of("nonExistField", ""),
                    Arguments.of("text", "badJson"));
        }

        @Test
        void shouldThrowBanException() {
            var patch = PatchReqBuilder.patchReq()
                    .withField("time")
                    .withValue("newTime").build();
            assertThrows(BanException.class, () -> service.patch(1L, patch, defaultPageable));
        }

        @Test
        void shouldThrowNotFoundElementException() {
            long nonExistId = -1L;
            var patch = PatchReqBuilder.patchReq()
                    .withField("text")
                    .withValue("newText").build();
            assertThrows(NotFoundElementException.class, () -> service.patch(nonExistId, patch, defaultPageable));
        }

        @ParameterizedTest
        @MethodSource("negativePatchRequest")
        void shouldThrowPatchException(String field, String value) {
            var patch = PatchReqBuilder.patchReq()
                    .withField(field)
                    .withValue(value).build();
            assertThrows(PatchException.class, () -> service.patch(1L, patch, defaultPageable));
        }

        @Test
        void shouldReturnExpected() {
            long id = 1L;
            String newValue = "newText";
            var patch = PatchReqBuilder.patchReq()
                    .withField("text")
                    .withValue(String.format("\"%s\"", newValue)).build();
            var actual = service.patch(id, patch, defaultPageable);
            assertThat(actual.text()).isEqualTo(newValue);
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

}