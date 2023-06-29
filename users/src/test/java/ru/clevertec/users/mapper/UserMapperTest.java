package ru.clevertec.users.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.security.core.GrantedAuthority;
import ru.clevertec.users.data.builder.impl.dto.auth.RegisterDtoBuilder;
import ru.clevertec.users.data.builder.impl.dto.users.UserFilterBuilder;
import ru.clevertec.users.data.builder.impl.dto.users.UserReqBuilder;
import ru.clevertec.users.data.builder.impl.entity.UserBuilder;
import ru.clevertec.users.dto.auth.RegisterDto;
import ru.clevertec.users.dto.users.UserFilter;
import ru.clevertec.users.dto.users.UserRequest;
import ru.clevertec.users.dto.users.UserResponse;
import ru.clevertec.users.entity.Role;
import ru.clevertec.users.entity.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void checkRegisterDtoToUser() {
        RegisterDto request = RegisterDtoBuilder.registerDto()
                .withUsername("usernameRegister")
                .withPassword("passwordRegister")
                .withFirstname("firstnameRegister")
                .withLastname("lastnameRegister")
                .withEmail("emailRegister")
                .build();
        User actual = mapper.toFrom(request);
        assertAll(
                () -> assertThat(actual.getUsername()).isEqualTo(request.username()),
                () -> assertThat(actual.getPassword()).isNotEqualTo(request.password()),
                () -> assertThat(actual.getRole()).isEqualTo(Role.SUBSCRIBER),
                () -> assertThat(actual.getFirstname()).isEqualTo(request.firstname()),
                () -> assertThat(actual.getLastname()).isEqualTo(request.lastname()),
                () -> assertThat(actual.getEmail()).isEqualTo(request.email()));
    }

    @Test
    void checkUserFilterToUser() {
        UserFilter filter = UserFilterBuilder.userFilter()
                .withUsername("usernameFilter")
                .withRole("ADMIN")
                .withFirstname("firstnameFilter")
                .withLastname("lastnameFilter")
                .withEmail("emailFilter")
                .build();
        User actual = mapper.toFrom(filter);
        assertAll(
                () -> assertThat(actual.getUsername()).isEqualTo(filter.username()),
                () -> assertThat(actual.getRole().name()).isEqualTo(filter.role()),
                () -> assertThat(actual.getFirstname()).isEqualTo(filter.firstname()),
                () -> assertThat(actual.getLastname()).isEqualTo(filter.lastname()),
                () -> assertThat(actual.getEmail()).isEqualTo(filter.email()));
    }

    @Test
    void checkUserToUserResponse() {
        User user = UserBuilder.user()
                .withUsername("usernameUser")
                .withRole(Role.ADMIN)
                .withFirstname("firstnameUser")
                .withLastname("lastnameUser")
                .withEmail("emailUser")
                .build();
        List<String> expectedRoles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        UserResponse actual = mapper.toFrom(user);
        assertAll(
                () -> assertThat(actual.username()).isEqualTo(user.getUsername()),
                () -> assertThat(actual.roles()).isEqualTo(expectedRoles),
                () -> assertThat(actual.firstname()).isEqualTo(user.getFirstname()),
                () -> assertThat(actual.lastname()).isEqualTo(user.getLastname()),
                () -> assertThat(actual.email()).isEqualTo(user.getEmail()));
    }

    @Test
    void checkUpdate() {
        String usernameBefore = "usernameUser";
        Role roleBefore = Role.ADMIN;
        User user = UserBuilder.user()
                .withUsername(usernameBefore)
                .withRole(roleBefore)
                .withFirstname("firstnameUser")
                .withLastname("lastnameUser")
                .withEmail("emailUser")
                .build();
        UserRequest request = UserReqBuilder.userReq()
                .withFirstname("firstnameRequest")
                .withLastname("lastnameRequest")
                .withEmail("emailRequest")
                .build();
        mapper.update(user, request);
        assertAll(
                () -> assertThat(user.getUsername()).isEqualTo(usernameBefore),
                () -> assertThat(user.getRole()).isEqualTo(roleBefore),
                () -> assertThat(user.getFirstname()).isEqualTo(request.firstname()),
                () -> assertThat(user.getLastname()).isEqualTo(request.lastname()),
                () -> assertThat(user.getEmail()).isEqualTo(request.email()));
    }

}