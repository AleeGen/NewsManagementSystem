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
import ru.clevertec.news.dto.comment.CommentRequest;
import ru.clevertec.news.dto.comment.CommentFilter;
import ru.clevertec.news.dto.comment.CommentResponse;
import ru.clevertec.news.entity.Comment;
import ru.clevertec.news.entity.News;
import ru.clevertec.news.repository.CommentRepository;
import ru.clevertec.news.service.CommentService;
import ru.clevertec.news.mapper.CommentMapper;
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
public class CommentServiceImpl implements CommentService {

    private final CommentRepository rep;
    private final CommentMapper mapper;
    private final String NOT_FOUND = "Comment with id = %d not found";
    private final Set<String> tabooPatchFields = Set.of("id", "time", "news");

    @Override
    @Caching(key = "id", method = CacheMethod.EXTRACT)
    public List<CommentResponse> findAll(CommentFilter filter, Pageable pageable) {
        return rep.findAll(Example.of(mapper.toFrom(filter)), pageable)
                .map(mapper::toFrom)
                .stream().toList();
    }

    @Override
    @Caching(key = "id")
    public CommentResponse findById(Long id) {
        return mapper.toFrom(rep.findById(id).orElseThrow(() ->
                new NotFoundElementException(String.format(NOT_FOUND, id), HttpStatus.NOT_FOUND.value())));
    }

    @Override
    @Caching(key = "id")
    public List<CommentResponse> findByNewsId(Long id, Pageable pageable) {
        return rep.findByNews_Id(id, pageable).map(mapper::toFrom).stream().toList();
    }

    @Override
    @Transactional
    @Caching(key = "id", method = CacheMethod.CHANGE, incoming = false)
    public CommentResponse save(CommentRequest request) {
        Comment comment = mapper.toFrom(request);
        comment.setTime(LocalDateTime.now());
        comment.setNews(News.builder().id(request.newsId()).build());
        return mapper.toFrom(rep.save(comment));
    }

    @Override
    @Transactional
    @Caching(key = "id", method = CacheMethod.CHANGE)
    public CommentResponse update(Long id, CommentRequest request) {
        Comment comment = rep.findById(id).orElseThrow(() ->
                new NotFoundElementException(String.format(NOT_FOUND, id), HttpStatus.NOT_ACCEPTABLE.value()));
        mapper.update(comment, request);
        return mapper.toFrom(comment);
    }

    @Override
    @Transactional
    @Caching(key = "id", method = CacheMethod.CHANGE)
    public CommentResponse patch(Long id, PatchRequest pr) {
        if (tabooPatchFields.contains(pr.field())) {
            throw new BanException(
                    String.format("You cannot edit the field '%s' yourself", pr.field()),
                    HttpStatus.FORBIDDEN.value());
        }
        Comment comment = rep.findById(id).orElseThrow(
                () -> new NotFoundElementException(String.format(NOT_FOUND, id), HttpStatus.NOT_ACCEPTABLE.value()));
        comment.setTime(LocalDateTime.now());
        PatchResponse patch = Patch.execute(pr, Comment.class);
        Field field = patch.modifiedField();
        field.setAccessible(true);
        ReflectionUtils.setField(field, comment, patch.value());
        return mapper.toFrom(comment);
    }

    @Override
    @Transactional
    @Caching(key = "id", method = CacheMethod.DELETE)
    public void delete(Long id) {
        Optional<Comment> comment = rep.findById(id);
        rep.delete(comment.orElseThrow(() ->
                new NotFoundElementException(String.format(NOT_FOUND, id), HttpStatus.NOT_ACCEPTABLE.value())));
    }

}