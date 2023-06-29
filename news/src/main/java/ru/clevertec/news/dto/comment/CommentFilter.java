package ru.clevertec.news.dto.comment;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentFilter(Long id,
                            LocalDateTime time,
                            String username,
                            String text,
                            Long newsId) {
}