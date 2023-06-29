package ru.clevertec.users.data.builder.impl.dto.users;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.users.data.builder.EntityBuilder;
import ru.clevertec.users.dto.users.UserRequest;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "userReq")
public class UserReqBuilder implements EntityBuilder<UserRequest> {

    private String firstname = "firstname";
    private String lastname = "lastname";
    private String email = "email@gmail.com";

    @Override
    public UserRequest build() {
        return UserRequest.builder()
                .firstname(firstname)
                .lastname(lastname)
                .email(email)
                .build();
    }

}