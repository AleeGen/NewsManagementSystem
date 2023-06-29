package ru.clevertec.users.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RegisterDto(@NotBlank
                          @Size(max = 25)
                          String username,

                          @NotBlank
                          @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,25}$")
                          String password,

                          @NotBlank
                          @Size(max = 25)
                          String firstname,

                          @NotBlank
                          @Size(max = 25)
                          String lastname,

                          @Email
                          @NotBlank
                          String email) {
}
