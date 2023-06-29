package ru.clevertec.news.integration.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.stream.Stream;

import ru.clevertec.exceptionhandlerstarter.exception.impl.BanException;
import ru.clevertec.exceptionhandlerstarter.exception.impl.NotFoundElementException;
import ru.clevertec.exceptionhandlerstarter.exception.impl.PatchException;
import ru.clevertec.news.data.builder.impl.dto.comments.CommentReqBuilder;
import ru.clevertec.news.data.builder.impl.dto.patch.PatchReqBuilder;
import ru.clevertec.news.dto.comment.CommentFilter;
import ru.clevertec.news.dto.comment.CommentResponse;
import ru.clevertec.news.integration.AbstractTestContainer;
import ru.clevertec.news.service.impl.CommentServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.clevertec.news.data.reader.CommentReader.findAllDefaultPageable;
import static ru.clevertec.news.data.reader.CommentReader.findAllWithFilter;
import static ru.clevertec.news.data.reader.CommentReader.findAllWithParametersPageable;
import static ru.clevertec.news.data.reader.CommentReader.findById;

@RequiredArgsConstructor
public class CommentServiceIT extends AbstractTestContainer {

    private final CommentServiceImpl service;

    @Nested
    class CheckFindAll {

        @Test
        @SneakyThrows
        void checkDefaultPageableShouldReturnExpected() {
            var expected = findAllDefaultPageable();
            var filter = CommentFilter.builder().build();
            var pageable = PageRequest.of(0, 20);
            var actual = service.findAll(filter, pageable);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @SneakyThrows
        void checkWithParametersPageableShouldReturnExpected() {
            var expected = findAllWithParametersPageable();
            var filter = CommentFilter.builder().build();
            var pageable = PageRequest.of(20, 5);
            var actual = service.findAll(filter, pageable);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @SneakyThrows
        void checkWithFilterShouldReturnExpected() {
            var expected = findAllWithFilter();
            var filter = CommentFilter.builder().username("username-41").build();
            var pageable = PageRequest.of(0, 20, Sort.by("time"));
            var actual = service.findAll(filter, pageable);
            assertThat(actual).isEqualTo(expected);
        }

    }

    @Nested
    class CheckFindById {

        private static Stream<Long> ids() {
            return Stream.of(1L, 34L, 98L, 117L, 161L, 200L);
        }

        @Test
        void shouldThrowNotFoundElementException() {
            long nonExistId = -1L;
            assertThrows(NotFoundElementException.class, () -> service.findById(nonExistId));
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("ids")
        void shouldReturnExpected(Long id) {
            var expected = findById(id);
            var actual = service.findById(id);
            assertThat(actual).isEqualTo(expected);
        }

    }

    @Nested
    class CheckSave {

        @Test
        void shouldSave() {
            var request = CommentReqBuilder.commentReq()
                    .withUsername("newUsername")
                    .withText("newText")
                    .withNewsId(1L).build();
            var actual = service.save(request);
            assertAll(
                    () -> assertThat(actual.id()).isNotNull(),
                    () -> assertThat(actual.time()).isNotNull(),
                    () -> assertThat(actual.username()).isEqualTo(request.username()),
                    () -> assertThat(actual.text()).isEqualTo(request.text()),
                    () -> assertThat(actual.newsId()).isEqualTo(request.newsId()));
        }

    }

    @Nested
    class CheckUpdate {

        @Test
        void shouldThrowNotFoundElementException() {
            long nonExistId = -1L;
            var request = CommentReqBuilder.commentReq().build();
            assertThrows(NotFoundElementException.class, () -> service.update(nonExistId, request));
        }

        @Test
        void shouldUpdate() {
            long id = 1L;
            var request = CommentReqBuilder.commentReq()
                    .withUsername("newUsername")
                    .withText("newText").build();
            CommentResponse actual = service.update(id, request);
            assertAll(
                    () -> assertThat(actual.id()).isEqualTo(id),
                    () -> assertThat(actual.time()).isNotNull(),
                    () -> assertThat(actual.username()).isEqualTo(request.username()),
                    () -> assertThat(actual.text()).isEqualTo(request.text()),
                    () -> assertThat(actual.newsId()).isEqualTo(request.newsId()));
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
            assertThrows(BanException.class, () -> service.patch(1L, patch));
        }

        @Test
        void shouldThrowNotFoundElementException() {
            long nonExistId = -1L;
            var patch = PatchReqBuilder.patchReq()
                    .withField("text")
                    .withValue("newText").build();
            assertThrows(NotFoundElementException.class, () -> service.patch(nonExistId, patch));
        }

        @ParameterizedTest
        @MethodSource("negativePatchRequest")
        void shouldThrowPatchException(String field, String value) {
            var patch = PatchReqBuilder.patchReq()
                    .withField(field)
                    .withValue(value).build();
            assertThrows(PatchException.class, () -> service.patch(1L, patch));
        }

        @Test
        void shouldReturnExpected() {
            long id = 1L;
            String newValue = "newText";
            var patch = PatchReqBuilder.patchReq()
                    .withField("text")
                    .withValue(String.format("\"%s\"", newValue)).build();
            var actual = service.patch(id, patch);
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