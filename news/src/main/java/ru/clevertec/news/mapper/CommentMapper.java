package ru.clevertec.news.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.clevertec.news.dto.comment.CommentRequest;
import ru.clevertec.news.dto.comment.CommentFilter;
import ru.clevertec.news.dto.comment.CommentResponse;
import ru.clevertec.news.entity.Comment;

import java.time.LocalDateTime;

@Mapper(imports = LocalDateTime.class)
public interface CommentMapper {

    Comment toFrom(CommentRequest request);

    @Mapping(target = "news", expression = "java(News.builder().id(filter.newsId()).build())")
    Comment toFrom(CommentFilter filter);

    @Mapping(target = "newsId", source = "comment.news.id")
    CommentResponse toFrom(Comment comment);

    @Mapping(target = "news", expression = "java(News.builder().id(response.newsId()).build())")
    Comment toFrom(CommentResponse response);

    @Mapping(target = "time", expression = "java(LocalDateTime.now())")
    void update(@MappingTarget Comment comment, CommentRequest request);

}