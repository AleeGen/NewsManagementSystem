package ru.clevertec.news.service.impl;

import java.util.Collections;
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
import ru.clevertec.news.data.builder.impl.dto.news.NewsReqBuilder;
import ru.clevertec.news.data.builder.impl.dto.patch.PatchReqBuilder;
import ru.clevertec.news.data.builder.impl.dto.comments.CommentResBuilder;
import ru.clevertec.news.data.builder.impl.dto.news.NewsResBuilder;
import ru.clevertec.news.data.builder.impl.entity.CommentBuilder;
import ru.clevertec.news.data.builder.impl.entity.NewsBuilder;
import ru.clevertec.news.dto.news.NewsFilter;
import ru.clevertec.news.entity.News;
import ru.clevertec.news.repository.NewsRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.news.mapper.CommentMapper;
import ru.clevertec.news.mapper.NewsMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NewsServiceImplTest {

    private final Pageable pageable = Pageable.unpaged();

    @InjectMocks
    private NewsServiceImpl serviceNews;

    @Mock
    private NewsRepository rep;

    @Mock
    private NewsMapper mapperNews;

    @Mock
    private CommentServiceImpl serviceComment;

    @Mock
    private CommentMapper commentMapper;

    @Captor
    private ArgumentCaptor<News> captor;

    @Nested
    class CheckFindAll {

        private static Page<News> news;

        @BeforeAll
        static void init() {
            news = new PageImpl<>(List.of(
                    NewsBuilder.news()
                            .withComments(List.of(
                                    CommentBuilder.comment().build(),
                                    CommentBuilder.comment().build())).build(),
                    NewsBuilder.news()
                            .withComments(List.of(
                                    CommentBuilder.comment().build(),
                                    CommentBuilder.comment().build(),
                                    CommentBuilder.comment().build(),
                                    CommentBuilder.comment().build())).build()));
        }

        @Test
        void shouldReturnExpectedSize() {
            int expected = news.getSize();
            doReturn(NewsBuilder.news().withId(null).build())
                    .when(mapperNews).toFrom(any(NewsFilter.class));
            var filter = NewsFilter.builder().build();
            doReturn(news).when(rep).findAll(any(Example.class), any(Pageable.class));
            int actual = serviceNews.findAll(filter, pageable).size();
            assertThat(actual).isEqualTo(expected);
        }

    }

    @Nested
    class CheckFindById {

        @Test
        void shouldThrowNotFoundElementException() {
            long id = -1L;
            doReturn(Optional.empty()).when(rep).findById(id);
            assertThrows(NotFoundElementException.class, () -> serviceNews.findById(id, pageable));
        }

        @Test
        void shouldReturnExpected() {
            long id = 1L;
            var expected = NewsBuilder.news()
                    .withComments(List.of(
                            CommentBuilder.comment().build(),
                            CommentBuilder.comment().build()))
                    .withId(id)
                    .build();
            doReturn(Optional.of(expected)).when(rep).findById(id);
            var response = NewsResBuilder.newsRes()
                    .withId(expected.getId())
                    .withTime(expected.getTime())
                    .withTitle(expected.getTitle())
                    .withText(expected.getText())
                    .withComments(expected.getComments().stream()
                            .map(c -> CommentResBuilder.commentRes().withId(c.getId()).build()).toList())
                    .build();
            doReturn(response).when(mapperNews).toFrom(expected);
            doReturn(response.comments()).when(serviceComment).findByNewsId(id, pageable);
            var actual = serviceNews.findById(id, pageable);
            assertAll(
                    () -> assertThat(actual.id()).isEqualTo(expected.getId()),
                    () -> assertThat(actual.time()).isEqualTo(expected.getTime()),
                    () -> assertThat(actual.title()).isEqualTo(expected.getTitle()),
                    () -> assertThat(actual.text()).isEqualTo(expected.getText()),
                    () -> assertThat(actual.comments().size()).isEqualTo(expected.getComments().size()));
        }

    }

    @Nested
    class CheckSave {

        @Test
        void shouldSave() {
            var request = NewsReqBuilder.newsReq().build();
            News news = NewsBuilder.news()
                    .withTitle(request.title())
                    .withText(request.text())
                    .build();
            doReturn(news).when(mapperNews).toFrom(request);
            serviceNews.save(request);
            verify(rep).save(captor.capture());
            News actual = captor.getValue();
            assertAll(
                    () -> assertThat(actual.getTime()).isNotNull(),
                    () -> assertThat(actual.getTitle()).isEqualTo(request.title()),
                    () -> assertThat(actual.getText()).isEqualTo(request.text()));

        }

    }

    @Nested
    class CheckUpdate {

        @Test
        void shouldThrowNotFoundElementException() {
            long id = -1L;
            var request = NewsReqBuilder.newsReq().build();
            doReturn(Optional.empty()).when(rep).findById(id);
            assertThrows(NotFoundElementException.class, () -> serviceNews.update(id, request, pageable));
        }

        @Test
        void shouldUpdate() {
            long id = -1L;
            var request = NewsReqBuilder.newsReq().build();
            var updated = NewsBuilder.news().withId(id).build();
            doReturn(Optional.of(updated)).when(rep).findById(id);
            doReturn(Collections.emptyList()).when(serviceComment).findByNewsId(id, pageable);
            serviceNews.update(id, request, pageable);
            verify(mapperNews).update(updated, request);
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
            assertThrows(BanException.class, () -> serviceNews.patch(1L, patch, pageable));
        }

        @Test
        void shouldThrowNotFoundElementException() {
            long nonExistId = 1L;
            var patch = PatchReqBuilder.patchReq()
                    .withField("text")
                    .withValue("newText").build();
            doReturn(Optional.empty()).when(rep).findById(nonExistId);
            assertThrows(NotFoundElementException.class, () -> serviceNews.patch(nonExistId, patch, pageable));
        }

        @ParameterizedTest
        @MethodSource("negativePatchRequest")
        void shouldThrowPatchException(String field, String value) {
            var patch = PatchReqBuilder.patchReq()
                    .withField(field)
                    .withValue(value).build();
            doReturn(Optional.of(NewsBuilder.news().build())).when(rep).findById(1L);
            assertThrows(PatchException.class, () -> serviceNews.patch(1L, patch, pageable));
        }

        @Test
        void shouldReturnExpected() {
            long id = 1L;
            String newValue = "newText";
            var patch = PatchReqBuilder.patchReq()
                    .withField("text")
                    .withValue(String.format("\"%s\"", newValue)).build();
            var news = NewsBuilder.news().build();
            doReturn(Optional.of(news)).when(rep).findById(id);
            var newsResponse = NewsResBuilder.newsRes().withText(newValue).build();
            doReturn(newsResponse).when(mapperNews).toFrom(news);
            var actual = serviceNews.patch(id, patch, pageable);
            assertThat(actual.text()).isEqualTo(newValue);
        }

    }

    @Nested
    class CheckDelete {

        @Test
        void shouldThrowNotFoundElementException() {
            doReturn(Optional.empty()).when(rep).findById(-1L);
            assertThrows(NotFoundElementException.class, () -> serviceNews.delete(-1L));
        }

        @Test
        void shouldDelete() {
            long id = 1L;
            var news = NewsBuilder.news().withId(id).build();
            doReturn(Optional.of(news)).when(rep).findById(id);
            assertAll(() -> assertDoesNotThrow(() -> serviceNews.delete(id)),
                    () -> verify(rep).delete(news));
        }

    }

}