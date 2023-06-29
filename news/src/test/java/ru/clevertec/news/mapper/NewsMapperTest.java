package ru.clevertec.news.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.news.data.builder.impl.dto.news.NewsReqBuilder;
import ru.clevertec.news.data.builder.impl.dto.news.NewsFilterBuilder;
import ru.clevertec.news.data.builder.impl.dto.comments.CommentResBuilder;
import ru.clevertec.news.data.builder.impl.dto.news.NewsResBuilder;
import ru.clevertec.news.data.builder.impl.entity.CommentBuilder;
import ru.clevertec.news.data.builder.impl.entity.NewsBuilder;
import ru.clevertec.news.dto.comment.CommentResponse;
import ru.clevertec.news.entity.Comment;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class NewsMapperTest {

    @InjectMocks
    private final NewsMapper mapperNews = Mappers.getMapper(NewsMapper.class);

    @Mock
    private CommentMapper mapperComment;

    @Test
    void checkRequestToEntity() {
        var request = NewsReqBuilder.newsReq()
                .withTitle("requestTitle")
                .withText("requestText")
                .build();
        var entity = mapperNews.toFrom(request);
        assertAll(
                () -> assertThat(request.title()).isEqualTo(entity.getTitle()),
                () -> assertThat(request.text()).isEqualTo(entity.getText()));
    }

    @Test
    void checkFilterToEntity() {
        var filter = NewsFilterBuilder.newsFilter()
                .withTime(LocalDateTime.now())
                .withTitle("filterTitle")
                .withText("filterText")
                .build();
        var entity = mapperNews.toFrom(filter);
        assertAll(
                () -> assertThat(filter.time()).isEqualTo(entity.getTime()),
                () -> assertThat(filter.title()).isEqualTo(entity.getTitle()),
                () -> assertThat(filter.text()).isEqualTo(entity.getText()));
    }

    @Test
    void checkEntityToResponse() {
        long newsId = -1L;
        var entity = NewsBuilder.news()
                .withId(newsId)
                .withTime(LocalDateTime.now())
                .withTitle("entityTitle")
                .withText("entityText")
                .build();
        var comments = List.of(
                CommentBuilder.comment()
                        .withId(1L)
                        .withUsername("username1")
                        .withText("text1")
                        .withNews(entity)
                        .build(),
                CommentBuilder.comment()
                        .withId(2L)
                        .withUsername("username1")
                        .withText("text1")
                        .withNews(entity)
                        .build());
        entity.setComments(comments);
        comments.forEach(c -> doReturn(CommentResBuilder.commentRes()
                .withId(c.getId())
                .withTime(c.getTime())
                .withUsername(c.getUsername())
                .withText(c.getText())
                .withNewsId(c.getNews().getId())
                .build())
                .when(mapperComment).toFrom(c));
        var response = mapperNews.toFrom(entity);
        var entityCommentIds = entity.getComments().stream().map(Comment::getId).toList();
        var responseCommentIds = response.comments().stream().map(CommentResponse::id).toList();
        assertAll(
                () -> assertThat(entity.getId()).isEqualTo(response.id()),
                () -> assertThat(entity.getTime()).isEqualTo(response.time()),
                () -> assertThat(entity.getTitle()).isEqualTo(response.title()),
                () -> assertThat(entity.getText()).isEqualTo(response.text()),
                () -> assertThat(entityCommentIds).isEqualTo(responseCommentIds));
    }

    @Test
    void checkResponseToEntity() {
        long newsId = -1L;
        var comments = List.of(
                CommentResBuilder.commentRes()
                        .withId(1L)
                        .withUsername("username1")
                        .withText("text1")
                        .withNewsId(newsId)
                        .build(),
                CommentResBuilder.commentRes()
                        .withId(2L)
                        .withUsername("username1")
                        .withText("text1")
                        .withNewsId(newsId)
                        .build());
        var response = NewsResBuilder.newsRes()
                .withId(newsId)
                .withTime(LocalDateTime.now())
                .withTitle("entityTitle")
                .withText("entityText")
                .withComments(comments)
                .build();
        comments.forEach(c -> doReturn(CommentBuilder.comment()
                .withId(c.id())
                .withTime(c.time())
                .withUsername(c.username())
                .withText(c.text())
                .withNews(NewsBuilder.news().withId(c.newsId()).build())
                .build())
                .when(mapperComment).toFrom(c));
        var entity = mapperNews.toFrom(response);
        var responseCommentIds = response.comments().stream().map(CommentResponse::id).toList();
        var entityCommentIds = entity.getComments().stream().map(Comment::getId).toList();
        assertAll(
                () -> assertThat(entity.getId()).isEqualTo(response.id()),
                () -> assertThat(entity.getTime()).isEqualTo(response.time()),
                () -> assertThat(entity.getTitle()).isEqualTo(response.title()),
                () -> assertThat(entity.getText()).isEqualTo(response.text()),
                () -> assertThat(entityCommentIds).isEqualTo(responseCommentIds));
    }

    @Test
    void checkUpdate() {
        var request = NewsReqBuilder.newsReq()
                .withTitle("requestTitle")
                .withText("requestText")
                .build();
        var entity = NewsBuilder.news()
                .withId(-1L)
                .withTime(LocalDateTime.of(1999, 1, 1, 1, 1))
                .withTitle("entityTitle")
                .withText("entityText")
                .build();
        var timeBefore = entity.getTime();
        mapperNews.update(entity, request);
        assertAll(
                () -> assertThat(entity.getTime()).isNotEqualTo(timeBefore),
                () -> assertThat(request.title()).isEqualTo(entity.getTitle()),
                () -> assertThat(request.text()).isEqualTo(entity.getText()));
    }

}