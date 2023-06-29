package ru.clevertec.news.controller;

import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.loggingstarter.annotation.Logging;
import ru.clevertec.news.controller.openapi.CommentOpenApi;
import ru.clevertec.news.dto.comment.CommentRequest;
import ru.clevertec.news.dto.comment.CommentFilter;
import ru.clevertec.news.dto.comment.CommentResponse;
import ru.clevertec.news.service.CommentService;
import ru.clevertec.news.util.patch.PatchRequest;

@Logging
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "comments")
public class CommentController implements CommentOpenApi {

    private final CommentService service;

    @Override
    @GetMapping
    public ResponseEntity<List<CommentResponse>> findAll(@Valid CommentFilter filter, Pageable pageable) {
        return new ResponseEntity<>(service.findAll(filter, pageable), HttpStatus.OK);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<CommentResponse> get(@PathVariable Long id) {
        return new ResponseEntity<>(service.findById(id), HttpStatus.OK);
    }

    @Override
    @PostMapping
    public ResponseEntity<CommentResponse> post(@RequestBody @Valid CommentRequest request) {
        return new ResponseEntity<>(service.save(request), HttpStatus.CREATED);
    }

    @Override
    @PutMapping("/{id}")
    @PreAuthorize("@commentServiceImpl.findById(#id).username().equals(principal.username)")
    public ResponseEntity<CommentResponse> put(@PathVariable Long id, @RequestBody @Valid CommentRequest request) {
        return new ResponseEntity<>(service.update(id, request), HttpStatus.CREATED);
    }

    @Override
    @PatchMapping("/{id}")
    @PreAuthorize("@commentServiceImpl.findById(#id).username().equals(principal.username)")
    public ResponseEntity<CommentResponse> patch(@PathVariable Long id, @RequestBody @Valid PatchRequest pr) {
        return new ResponseEntity<>(service.patch(id, pr), HttpStatus.CREATED);
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("@commentServiceImpl.findById(#id).username().equals(principal.username)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}