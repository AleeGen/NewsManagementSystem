package ru.clevertec.news.data.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.experimental.UtilityClass;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import ru.clevertec.news.dto.news.NewsResponse;
import ru.clevertec.news.dto.news.NewsWithoutCommentResponse;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@UtilityClass
public class NewsReader {

    private final ObjectMapper mapper = new Jackson2ObjectMapperBuilder().deserializerByType(LocalDateTime.class,
            new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss:SSS"))).build();

    public static List<NewsWithoutCommentResponse> findAllWithParametersPageable() throws IOException {
        return getList("find_all/page-2-size-5.json");
    }

    public static List<NewsWithoutCommentResponse> findAllDefaultPageable() throws IOException {
        return getList("find_all/default-pageable.json");
    }

    public static List<NewsWithoutCommentResponse> findAllWithFilter() throws IOException {
        return getList("find_all/filter-by-title.json");
    }

    public static NewsResponse findByIdDefaultPageable(Long id) throws IOException {
        return get(String.format("find_by_id/default_pageable/%d.json", id));
    }

    public static NewsResponse findByIdWithParametersPageable() throws IOException {
        return get("find_by_id/id_1_page_3_size_2.json");
    }

    private static List<NewsWithoutCommentResponse> getList(String path) throws IOException {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("data/news_expected/" + path);
        return mapper.readValue(inputStream, new TypeReference<>() {
        });
    }

    private static NewsResponse get(String path) throws IOException {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("data/news_expected/" + path);
        return mapper.readValue(inputStream, NewsResponse.class);
    }

}