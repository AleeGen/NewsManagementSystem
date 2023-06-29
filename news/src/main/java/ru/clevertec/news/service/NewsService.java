package ru.clevertec.news.service;

import org.springframework.data.domain.Pageable;
import ru.clevertec.news.dto.news.NewsRequest;
import ru.clevertec.news.dto.news.NewsFilter;
import ru.clevertec.news.dto.news.NewsResponse;
import ru.clevertec.news.dto.news.NewsWithoutCommentResponse;
import ru.clevertec.news.util.patch.PatchRequest;

import java.util.List;

public interface NewsService {

    List<NewsWithoutCommentResponse> findAll(NewsFilter filter, Pageable pageable);

    NewsResponse findById(Long id, Pageable pageable);
    NewsResponse findById(Long id);

    NewsResponse save(NewsRequest request);

    NewsResponse update(Long id, NewsRequest request, Pageable pageable);

    NewsResponse patch(Long id, PatchRequest pr, Pageable pageable);

    void delete(Long id);

}