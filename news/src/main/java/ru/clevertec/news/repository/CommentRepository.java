package ru.clevertec.news.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.clevertec.news.entity.Comment;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Override
    @Cacheable(cacheNames = "cache", key = "#aLong")
    Optional<Comment> findById(Long aLong);

    @Cacheable(cacheNames = "cache", key = "#id")
    Page<Comment> findByNews_Id(Long id, Pageable pageable);

}