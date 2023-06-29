package ru.clevertec.news.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.clevertec.exceptionhandlerstarter.exception.impl.BanException;
import ru.clevertec.exceptionhandlerstarter.exception.impl.NotFoundElementException;
import ru.clevertec.exceptionhandlerstarter.exception.impl.PatchException;
import ru.clevertec.news.data.builder.impl.dto.comments.CommentReqBuilder;
import ru.clevertec.news.data.builder.impl.dto.patch.PatchReqBuilder;
import ru.clevertec.news.data.builder.impl.dto.comments.CommentResBuilder;
import ru.clevertec.news.data.builder.impl.entity.CommentBuilder;
import ru.clevertec.news.data.builder.impl.entity.NewsBuilder;
import ru.clevertec.news.dto.comment.CommentFilter;
import ru.clevertec.news.entity.Comment;
import ru.clevertec.news.repository.CommentRepository;
import ru.clevertec.news.mapper.CommentMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @InjectMocks
    private CommentServiceImpl service;

    @Mock
    private CommentRepository rep;

    @Mock
    private CommentMapper mapper;

    @Captor
    private ArgumentCaptor<Comment> captor;

    @Nested
    class CheckFindAll {

        private static Page<Comment> comments;

        @BeforeAll
        static void init() {
            comments = new PageImpl<>(List.of(
                    CommentBuilder.comment().build(),
                    CommentBuilder.comment().build(),
                    CommentBuilder.comment().build()));
        }

        @Test
        void shouldReturnExpectedSize() {
            int expected = comments.getSize();
            doReturn(CommentBuilder.comment().withId(null).build())
                    .when(mapper).toFrom(any(CommentFilter.class));
            var filter = CommentFilter.builder().build();
            doReturn(comments).when(rep).findAll(any(Example.class), any(Pageable.class));
            int actual = service.findAll(filter, Pageable.unpaged()).size();
            assertThat(actual).isEqualTo(expected);
        }

    }

    @Nested
    class CheckFindById {

        @Test
        void shouldThrowNotFoundElementException() {
            long id = -1L;
            doReturn(Optional.empty()).when(rep).findById(id);
            assertThrows(NotFoundElementException.class, () -> service.findById(id));
        }

        @Test
        void shouldReturnExpected() {
            long id = 1L;
            var expected = CommentBuilder.comment().withId(id).build();
            doReturn(Optional.of(expected)).when(rep).findById(id);
            var response = CommentResBuilder.commentRes()
                    .withId(expected.getId())
                    .withTime(expected.getTime())
                    .withUsername(expected.getUsername())
                    .withText(expected.getText())
                    .build();
            doReturn(response).when(mapper).toFrom(expected);
            var actual = service.findById(id);
            assertAll(
                    () -> assertThat(actual.id()).isEqualTo(expected.getId()),
                    () -> assertThat(actual.time()).isEqualTo(expected.getTime()),
                    () -> assertThat(actual.username()).isEqualTo(expected.getUsername()),
                    () -> assertThat(actual.text()).isEqualTo(expected.getText()));
        }

    }

    @Nested
    class CheckSave {

        @Test
        void shouldSave() {
            var request = CommentReqBuilder.commentReq().build();
            Comment fromMapping = CommentBuilder.comment()
                    .withUsername(request.username())
                    .withText(request.text())
                    .withNews(NewsBuilder.news().withId(request.newsId()).build())
                    .build();
            doReturn(fromMapping).when(mapper).toFrom(request);
            service.save(request);
            verify(rep).save(captor.capture());
            Comment actual = captor.getValue();
            assertAll(
                    () -> assertThat(actual.getTime()).isNotNull(),
                    () -> assertThat(actual.getUsername()).isEqualTo(request.username()),
                    () -> assertThat(actual.getText()).isEqualTo(request.text()),
                    () -> assertThat(actual.getNews().getId()).isEqualTo(request.newsId()));
        }

    }

    @Nested
    class CheckUpdate {

        @Test
        void shouldThrowNotFoundElementException() {
            long id = -1L;
            var request = CommentReqBuilder.commentReq().build();
            doReturn(Optional.empty()).when(rep).findById(id);
            assertThrows(NotFoundElementException.class, () -> service.update(id, request));
        }

        @Test
        void shouldUpdate() {
            long id = -1L;
            var request = CommentReqBuilder.commentReq().build();
            var updated = CommentBuilder.comment().withId(id).build();
            doReturn(Optional.of(updated)).when(rep).findById(id);
            service.update(id, request);
            verify(mapper).update(updated, request);
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
            long nonExistId = 1L;
            var patch = PatchReqBuilder.patchReq()
                    .withField("text")
                    .withValue("newText").build();
            doReturn(Optional.empty()).when(rep).findById(nonExistId);
            assertThrows(NotFoundElementException.class, () -> service.patch(nonExistId, patch));
        }

        @ParameterizedTest
        @MethodSource("negativePatchRequest")
        void shouldThrowPatchException(String field, String value) {
            var patch = PatchReqBuilder.patchReq()
                    .withField(field)
                    .withValue(value).build();
            doReturn(Optional.of(CommentBuilder.comment().build())).when(rep).findById(1L);
            assertThrows(PatchException.class, () -> service.patch(1L, patch));
        }

        @Test
        void shouldReturnExpected() {
            long id = 1L;
            String newValue = "newText";
            var patch = PatchReqBuilder.patchReq()
                    .withField("text")
                    .withValue(String.format("\"%s\"", newValue)).build();
            var comment = CommentBuilder.comment().build();
            doReturn(Optional.of(comment)).when(rep).findById(id);
            var commentResponse = CommentResBuilder.commentRes().withText(newValue).build();
            doReturn(commentResponse).when(mapper).toFrom(comment);
            var actual = service.patch(id, patch);
            assertThat(actual.text()).isEqualTo(newValue);
        }

    }

    @Nested
    class CheckDelete {

        @Test
        void shouldThrowNotFoundElementException() {
            doReturn(Optional.empty()).when(rep).findById(-1L);
            assertThrows(NotFoundElementException.class, () -> service.delete(-1L));
        }

        @Test
        void shouldDelete() {
            long id = 1L;
            var tag = CommentBuilder.comment().withId(id).build();
            doReturn(Optional.of(tag)).when(rep).findById(id);
            assertAll(() -> assertDoesNotThrow(() -> service.delete(id)),
                    () -> verify(rep).delete(tag));
        }

    }

}