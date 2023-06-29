package ru.clevertec.news.dto.news;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NewsFilter(String username,
                         LocalDateTime time,
                         String title,
                         String text) {
}