package ru.clevertec.news.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.clevertec.news.entity.News;

import java.util.Optional;

public interface NewsRepository extends JpaRepository<News, Long> {

    @Override
    @Cacheable(cacheNames = "cache", key = "#aLong")
    Optional<News> findById(Long aLong);

}