package ru.clevertec.news.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
public record CommentResponse(Long id,

                              @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss:SSS")
                              LocalDateTime time,

                              String username,
                              String text,
                              Long newsId) implements Serializable {
}