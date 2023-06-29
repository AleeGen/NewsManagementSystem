package ru.clevertec.users.data.builder.impl.dto.auth;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.users.data.builder.EntityBuilder;
import ru.clevertec.users.dto.auth.AuthDto;
import ru.clevertec.users.entity.Role;

import java.util.Collection;
import java.util.List;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "authDto")
public class AuthDtoBuilder implements EntityBuilder<AuthDto> {

    private String username = "username";
    private Collection<String> authorities = List.of(Role.SUBSCRIBER.name());

    @Override
    public AuthDto build() {
        return new AuthDto(username, authorities);
    }

}