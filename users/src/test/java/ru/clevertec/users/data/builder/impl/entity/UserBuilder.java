package ru.clevertec.users.data.builder.impl.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.users.data.builder.EntityBuilder;
import ru.clevertec.users.entity.Role;
import ru.clevertec.users.entity.User;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "user")
public class UserBuilder implements EntityBuilder<User> {

    private Long id = 1L;
    private String username = "username-0";
    private String password = "pass";
    private Role role = Role.SUBSCRIBER;
    private String firstname = "firstname";
    private String lastname = "lastname";
    private String email = "email";

    @Override
    public User build() {
        return User.builder()
                .id(id)
                .username(username)
                .password(password)
                .role(role)
                .firstname(firstname)
                .lastname(lastname)
                .email(email)
                .build();
    }

}