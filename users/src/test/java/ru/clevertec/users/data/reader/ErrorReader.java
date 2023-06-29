package ru.clevertec.users.data.reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import ru.clevertec.exceptionhandlerstarter.handler.response.impl.ExceptionResponse;

import java.io.InputStream;

@UtilityClass
public class ErrorReader {

    private final ObjectMapper mapper = new ObjectMapper();

    public static ExceptionResponse requiredAuthForAuth() {
        return read("required-auth-for-auth.json");
    }

    public static ExceptionResponse requiredAuthForAboutMe() {
        return read("required-auth-for-about-me.json");
    }

    public static ExceptionResponse invalidSyntax() {
        return read("invalid-syntax.json");
    }

    public static ExceptionResponse invalidUser() {
        return read("invalid-user.json");
    }

    public static ExceptionResponse notFoundUserByUsername() {
        return read("not-found-user-by-username.json");
    }

    public static ExceptionResponse notFoundUserById() {
        return read("not-found-user-by-id.json");
    }

    public static ExceptionResponse invalidPassword() {
        return read("invalid-password.json");
    }

    public static ExceptionResponse invalidRole() {
        return read("invalid-role-user.json");
    }

    @SneakyThrows
    private static ExceptionResponse read(String path) {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("data/error_expected/" + path);
        return mapper.readValue(inputStream, ExceptionResponse.class);
    }

}