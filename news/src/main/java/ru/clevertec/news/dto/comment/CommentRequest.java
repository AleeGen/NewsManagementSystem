package ru.clevertec.news.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record CommentRequest(@NotBlank
                             String username,

                             @Size(max = 200)
                             @NotBlank
                             String text,

                             @NotNull
                             @Positive
                             Long newsId) implements Serializable {
}