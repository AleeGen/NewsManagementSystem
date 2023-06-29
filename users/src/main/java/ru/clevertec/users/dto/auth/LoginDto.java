package ru.clevertec.users.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginDto(@NotBlank
                       String username,
                       @NotBlank
                       String password) {
}