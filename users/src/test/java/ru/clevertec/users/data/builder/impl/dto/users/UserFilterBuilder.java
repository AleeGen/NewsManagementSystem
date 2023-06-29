package ru.clevertec.users.data.builder.impl.dto.users;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.users.data.builder.EntityBuilder;
import ru.clevertec.users.dto.users.UserFilter;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "userFilter")
public class UserFilterBuilder implements EntityBuilder<UserFilter> {

    private String username;
    private String role;
    private String firstname;
    private String lastname;
    private String email;

    @Override
    public UserFilter build() {
        return UserFilter.builder()
                .username(username)
                .firstname(firstname)
                .lastname(lastname)
                .email(email)
                .role(role)
                .build();
    }

}