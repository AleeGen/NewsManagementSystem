package ru.clevertec.users.data.builder.impl.dto.auth;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.users.data.builder.EntityBuilder;
import ru.clevertec.users.dto.auth.LoginDto;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "loginDto")
public class LoginDtoBuilder implements EntityBuilder<LoginDto> {

    private String username = "username";
    private String password = "password123";

    @Override
    public LoginDto build() {
        return new LoginDto(username, password);
    }

}