package ru.clevertec.news.dto.users;

import lombok.Builder;

import java.util.List;

@Builder
public record UserResponse(String username,
                           List<String> roles,
                           String firstname,
                           String lastname,
                           String email) {
}