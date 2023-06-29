package ru.clevertec.news.dto.news;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
public record NewsWithoutCommentResponse(Long id,
                                         String username,

                                         @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss:SSS")
                                         LocalDateTime time,

                                         String title,
                                         String text) implements Serializable {
}