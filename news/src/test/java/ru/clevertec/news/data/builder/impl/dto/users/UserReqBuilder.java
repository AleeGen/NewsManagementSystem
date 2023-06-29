package ru.clevertec.news.data.builder.impl.dto.users;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.news.data.builder.EntityBuilder;
import ru.clevertec.news.dto.users.UserRequest;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "userReq")
public class UserReqBuilder implements EntityBuilder<UserRequest> {

    private String firstname = "firstname";
    private String lastname = "lastname";
    private String email = "email";

    @Override
    public UserRequest build() {
        return UserRequest.builder()
                .firstname(firstname)
                .lastname(lastname)
                .email(email)
                .build();
    }

}