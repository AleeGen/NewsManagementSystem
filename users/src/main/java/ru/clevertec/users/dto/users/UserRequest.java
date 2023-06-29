package ru.clevertec.users.dto.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserRequest(@NotBlank
                          @Size(max = 25)
                          String firstname,

                          @NotBlank
                          @Size(max = 25)
                          String lastname,

                          @Email
                          @NotBlank
                          String email) {
}