package ru.clevertec.users.dto.users;

import lombok.Builder;

@Builder
public record UserFilter(Long id,
                         String username,
                         String firstname,
                         String lastname,
                         String email,
                         String role) {
}