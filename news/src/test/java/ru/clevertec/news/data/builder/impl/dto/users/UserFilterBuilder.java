package ru.clevertec.news.data.builder.impl.dto.users;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.news.data.builder.EntityBuilder;
import ru.clevertec.news.dto.users.UserFilter;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "userFilter")
public class UserFilterBuilder implements EntityBuilder<UserFilter> {

    private String username = "username";
    private String role = "SUBSCRIBER";
    private String firstname = "firstname";
    private String lastname = "lastname";
    private String email = "email";

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