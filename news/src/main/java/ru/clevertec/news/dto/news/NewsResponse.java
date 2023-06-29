package ru.clevertec.news.dto.news;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import ru.clevertec.news.dto.comment.CommentResponse;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record NewsResponse(Long id,
                           String username,

                           @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss:SSS")
                           LocalDateTime time,

                           String title,
                           String text,
                           List<CommentResponse> comments) implements Serializable {
}