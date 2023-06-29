package ru.clevertec.news.data.builder.impl.dto.users;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.news.data.builder.EntityBuilder;
import ru.clevertec.news.dto.users.UserResponse;

import java.util.List;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "userRes")
public class UserResBuilder implements EntityBuilder<UserResponse> {

    private String username = "username";
    private List<String> roles = List.of("SUBSCRIBER");
    private String firstname = "firstname";
    private String lastname = "lastname";
    private String email = "email";

    @Override
    public UserResponse build() {
        return UserResponse.builder()
                .username(username)
                .roles(roles)
                .firstname(firstname)
                .lastname(lastname)
                .email(email)
                .build();
    }

}