package ru.clevertec.news.data.builder.impl.dto.auth;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.news.data.builder.EntityBuilder;
import ru.clevertec.news.dto.auth.RegisterDto;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "registerDto")
public class RegisterDtoBuilder implements EntityBuilder<RegisterDto> {

    private String username = "username";
    private String password = "password";
    private String firstname = "firstname";
    private String lastname = "lastname";
    private String email = "email";

    @Override
    public RegisterDto build() {
        return RegisterDto.builder()
                .username(username)
                .password(password)
                .firstname(firstname)
                .lastname(lastname)
                .email(email)
                .build();
    }

}