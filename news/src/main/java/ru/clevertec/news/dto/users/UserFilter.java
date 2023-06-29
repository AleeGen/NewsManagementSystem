package ru.clevertec.news.dto.users;

import lombok.Builder;

@Builder
public record UserFilter(String username,
                         String firstname,
                         String lastname,
                         String email,
                         String role) {
}