package ru.clevertec.users.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record TokenDto(@NotBlank
                       String token) {
}