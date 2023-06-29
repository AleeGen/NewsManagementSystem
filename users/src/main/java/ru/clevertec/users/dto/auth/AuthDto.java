package ru.clevertec.users.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Collection;

public record AuthDto(@NotBlank
                      String username,

                      @NotEmpty
                      Collection<String> authorities) {
}