package ru.clevertec.news.dto.auth;

import lombok.Builder;

@Builder
public record RegisterDto(String username,
                          String password,
                          String firstname,
                          String lastname,
                          String email) {
}