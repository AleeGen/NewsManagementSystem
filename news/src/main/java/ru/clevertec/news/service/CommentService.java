package ru.clevertec.news.service;

import org.springframework.data.domain.Pageable;
import ru.clevertec.news.dto.comment.CommentRequest;
import ru.clevertec.news.dto.comment.CommentFilter;
import ru.clevertec.news.dto.comment.CommentResponse;
import ru.clevertec.news.util.patch.PatchRequest;

import java.util.List;

public interface CommentService {

    List<CommentResponse> findAll(CommentFilter filter, Pageable pageable);

    CommentResponse findById(Long id);

    CommentResponse save(CommentRequest t);

    CommentResponse update(Long id, CommentRequest t);

    CommentResponse patch(Long id, PatchRequest pr);

    void delete(Long id);

    List<CommentResponse> findByNewsId(Long id, Pageable pageable);

}