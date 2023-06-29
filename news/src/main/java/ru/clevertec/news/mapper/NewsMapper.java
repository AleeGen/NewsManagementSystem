package ru.clevertec.news.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.clevertec.news.dto.news.NewsRequest;
import ru.clevertec.news.dto.news.NewsFilter;
import ru.clevertec.news.dto.news.NewsResponse;
import ru.clevertec.news.dto.news.NewsWithoutCommentResponse;
import ru.clevertec.news.entity.News;

import java.time.LocalDateTime;

@Mapper(uses = CommentMapper.class, imports = LocalDateTime.class)
public interface NewsMapper {

    News toFrom(NewsRequest request);

    News toFrom(NewsFilter filter);

    NewsResponse toFrom(News news);

    NewsWithoutCommentResponse toFromWithoutComment(News news);

    News toFrom(NewsResponse response);

    @Mapping(target = "time", expression = "java(LocalDateTime.now())")
    void update(@MappingTarget News news, NewsRequest request);

}