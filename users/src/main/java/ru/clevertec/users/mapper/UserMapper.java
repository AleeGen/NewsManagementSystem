package ru.clevertec.users.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.security.core.GrantedAuthority;
import ru.clevertec.users.dto.auth.RegisterDto;
import ru.clevertec.users.dto.users.UserFilter;
import ru.clevertec.users.dto.users.UserRequest;
import ru.clevertec.users.dto.users.UserResponse;
import ru.clevertec.users.entity.User;

@Mapper(imports = GrantedAuthority.class)
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", expression = "java(Role.SUBSCRIBER)")
    User toFrom(RegisterDto registerDto);

    User toFrom(UserFilter filter);

    @Mapping(target = "roles",
            expression = "java(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())")
    UserResponse toFrom(User user);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    void update(@MappingTarget User user, UserRequest request);

}