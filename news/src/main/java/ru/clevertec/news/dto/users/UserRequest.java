package ru.clevertec.news.dto.users;

import lombok.Builder;

@Builder
public record UserRequest(String firstname,
                          String lastname,
                          String email) {
}