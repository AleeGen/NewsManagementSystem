package ru.clevertec.news.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;
import ru.clevertec.cachestarter.annotation.CacheMethod;
import ru.clevertec.cachestarter.annotation.Caching;
import ru.clevertec.exceptionhandlerstarter.exception.impl.BanException;
import ru.clevertec.exceptionhandlerstarter.exception.impl.NotFoundElementException;
import ru.clevertec.news.dto.news.NewsRequest;
import ru.clevertec.news.dto.news.NewsFilter;
import ru.clevertec.news.dto.news.NewsResponse;
import ru.clevertec.news.dto.news.NewsWithoutCommentResponse;
import ru.clevertec.news.entity.Comment;
import ru.clevertec.news.entity.News;
import ru.clevertec.news.repository.NewsRepository;
import ru.clevertec.news.service.CommentService;
import ru.clevertec.news.service.NewsService;
import ru.clevertec.news.mapper.CommentMapper;
import ru.clevertec.news.mapper.NewsMapper;
import ru.clevertec.news.util.patch.Patch;
import ru.clevertec.news.util.patch.PatchRequest;
import ru.clevertec.news.util.patch.PatchResponse;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsServiceImpl implements NewsService {

    private final NewsRepository rep;
    private final NewsMapper mapper;
    private final CommentMapper commentMapper;
    private final CommentService commentService;
    private final String NOT_FOUND = "News with id = %d not found";
    private final Set<String> tabooPatchFields = Set.of("id", "time", "comments");

    @Override
    @Caching(key = "id", method = CacheMethod.EXTRACT)
    public List<NewsWithoutCommentResponse> findAll(NewsFilter filter, Pageable pageable) {
        return rep.findAll(Example.of(mapper.toFrom(filter)), pageable)
                .map(mapper::toFromWithoutComment)
                .stream().toList();
    }

    @Override
    @Caching(key = "id")
    public NewsResponse findById(Long id, Pageable pageable) {
        News news = rep.findById(id).orElseThrow(() ->
                new NotFoundElementException(String.format(NOT_FOUND, id), HttpStatus.NOT_FOUND.value()));
        List<Comment> comments = commentService.findByNewsId(id, pageable)
                .stream().map(commentMapper::toFrom).toList();
        news.setComments(comments);
        return mapper.toFrom(news);
    }

    @Override
    public NewsResponse findById(Long id) {
        News news = rep.findById(id).orElseThrow(() ->
                new NotFoundElementException(String.format(NOT_FOUND, id), HttpStatus.NOT_FOUND.value()));
        return mapper.toFrom(news);
    }

    @Override
    @Transactional
    @Caching(key = "id", method = CacheMethod.CHANGE, incoming = false)
    public NewsResponse save(NewsRequest request) {
        News news = mapper.toFrom(request);
        news.setTime(LocalDateTime.now());
        return mapper.toFrom(rep.save(news));
    }

    @Override
    @Transactional
    @Caching(key = "id", method = CacheMethod.CHANGE)
    public NewsResponse update(Long id, NewsRequest request, Pageable pageable) {
        News news = rep.findById(id).orElseThrow(() ->
                new NotFoundElementException(String.format(NOT_FOUND, id), HttpStatus.NOT_ACCEPTABLE.value()));
        List<Comment> comments = commentService.findByNewsId(id, pageable)
                .stream().map(commentMapper::toFrom).toList();
        news.setComments(comments);
        mapper.update(news, request);
        return mapper.toFrom(news);
    }

    @Override
    @Transactional
    @Caching(key = "id", method = CacheMethod.CHANGE)
    public NewsResponse patch(Long id, PatchRequest pr, Pageable pageable) {
        if (tabooPatchFields.contains(pr.field())) {
            throw new BanException(
                    String.format("You cannot edit the field '%s' yourself", pr.field()),
                    HttpStatus.FORBIDDEN.value());
        }
        News news = rep.findById(id).orElseThrow(
                () -> new NotFoundElementException(String.format(NOT_FOUND, id), HttpStatus.NOT_ACCEPTABLE.value()));
        news.setTime(LocalDateTime.now());
        PatchResponse patch = Patch.execute(pr, News.class);
        Field field = patch.modifiedField();
        field.setAccessible(true);
        ReflectionUtils.setField(field, news, patch.value());
        List<Comment> comments = commentService.findByNewsId(id, pageable)
                .stream().map(commentMapper::toFrom).toList();
        news.setComments(comments);
        return mapper.toFrom(news);
    }

    @Override
    @Transactional
    @Caching(key = "id", method = CacheMethod.DELETE)
    public void delete(Long id) {
        Optional<News> news = rep.findById(id);
        rep.delete(news.orElseThrow(() ->
                new NotFoundElementException(String.format(NOT_FOUND, id), HttpStatus.NOT_ACCEPTABLE.value())));
    }

}