package ru.clevertec.news.dto.news;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record NewsRequest(String username,

                          @Size(max = 70)
                          @NotBlank
                          String title,

                          @Size(max = 1000)
                          @NotBlank
                          String text) implements Serializable {
}