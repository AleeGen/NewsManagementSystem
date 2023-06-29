package ru.clevertec.news.mapper;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.clevertec.news.data.builder.impl.dto.comments.CommentReqBuilder;
import ru.clevertec.news.data.builder.impl.dto.comments.CommentFilterBuilder;
import ru.clevertec.news.data.builder.impl.entity.CommentBuilder;
import ru.clevertec.news.data.builder.impl.entity.NewsBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class CommentMapperTest {

    private final CommentMapper mapper = Mappers.getMapper(CommentMapper.class);

    @Test
    void checkRequestToEntity() {
        var request = CommentReqBuilder.commentReq()
                .withUsername("requestUsername")
                .withText("requestText")
                .build();
        var entity = mapper.toFrom(request);
        assertAll(
                () -> assertThat(request.username()).isEqualTo(entity.getUsername()),
                () -> assertThat(request.text()).isEqualTo(entity.getText()));
    }

    @Test
    void checkFilterToEntity() {
        var filter = CommentFilterBuilder.commentFilter()
                .withId(-1L)
                .withTime(LocalDateTime.now())
                .withUsername("filterUsername")
                .withText("filterText")
                .withNewsId(-2L)
                .build();
        var entity = mapper.toFrom(filter);
        assertAll(
                () -> assertThat(filter.id()).isEqualTo(entity.getId()),
                () -> assertThat(filter.time()).isEqualTo(entity.getTime()),
                () -> assertThat(filter.username()).isEqualTo(entity.getUsername()),
                () -> assertThat(filter.text()).isEqualTo(entity.getText()),
                () -> assertThat(filter.newsId()).isEqualTo(entity.getNews().getId()));
    }

    @Test
    void checkEntityToResponse() {
        var entity = CommentBuilder.comment()
                .withId(-1L)
                .withTime(LocalDateTime.now())
                .withUsername("entityUsername")
                .withText("entityText")
                .withNews(NewsBuilder.news().withId(-2L).build())
                .build();
        var response = mapper.toFrom(entity);
        assertAll(
                () -> assertThat(entity.getId()).isEqualTo(response.id()),
                () -> assertThat(entity.getTime()).isEqualTo(response.time()),
                () -> assertThat(entity.getUsername()).isEqualTo(response.username()),
                () -> assertThat(entity.getText()).isEqualTo(response.text()),
                () -> assertThat(entity.getNews().getId()).isEqualTo(response.newsId()));
    }

    @Test
    void checkUpdate() {
        var request = CommentReqBuilder.commentReq()
                .withUsername("requestUsername")
                .withText("requestText")
                .build();
        var entity = CommentBuilder.comment()
                .withTime(LocalDateTime.of(1999, 1, 1, 1, 1))
                .withUsername("entityUsername")
                .withText("entityText")
                .build();
        var timeBefore = entity.getTime();
        mapper.update(entity, request);
        assertAll(
                () -> assertThat(entity.getTime()).isNotEqualTo(timeBefore),
                () -> assertThat(request.username()).isEqualTo(entity.getUsername()),
                () -> assertThat(request.text()).isEqualTo(entity.getText()));
    }

}