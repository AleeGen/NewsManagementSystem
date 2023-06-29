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
import ru.clevertec.news.controller.openapi.NewsOpenApi;
import ru.clevertec.news.dto.news.NewsRequest;
import ru.clevertec.news.dto.news.NewsFilter;
import ru.clevertec.news.dto.news.NewsResponse;
import ru.clevertec.news.dto.news.NewsWithoutCommentResponse;
import ru.clevertec.news.service.NewsService;
import ru.clevertec.news.util.patch.PatchRequest;

@Logging
@RestController
@RequiredArgsConstructor
@RequestMapping("news")
public class NewsController implements NewsOpenApi {

    private final NewsService service;

    @Override
    @GetMapping
    public ResponseEntity<List<NewsWithoutCommentResponse>> findAll(NewsFilter filter,
                                                                    Pageable pageable) {
        return new ResponseEntity<>(service.findAll(filter, pageable), HttpStatus.OK);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<NewsResponse> get(@PathVariable Long id,
                                            Pageable pageable) {
        return new ResponseEntity<>(service.findById(id, pageable), HttpStatus.OK);
    }

    @Override
    @PostMapping
    public ResponseEntity<NewsResponse> post(@RequestBody @Valid NewsRequest request) {
        return new ResponseEntity<>(service.save(request), HttpStatus.CREATED);
    }

    @Override
    @PutMapping("/{id}")
    @PreAuthorize("@newsServiceImpl.findById(#id).username().equals(principal.username)")
    public ResponseEntity<NewsResponse> put(@PathVariable Long id,
                                            @RequestBody @Valid NewsRequest request,
                                            Pageable pageable) {
        return new ResponseEntity<>(service.update(id, request, pageable), HttpStatus.CREATED);
    }

    @Override
    @PatchMapping("/{id}")
    @PreAuthorize("@newsServiceImpl.findById(#id).username().equals(principal.username)")
    public ResponseEntity<NewsResponse> patch(@PathVariable Long id,
                                              @RequestBody @Valid PatchRequest pr,
                                              Pageable pageable) {
        return new ResponseEntity<>(service.patch(id, pr, pageable), HttpStatus.CREATED);
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') || @newsServiceImpl.findById(#id).username().equals(principal.username)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}