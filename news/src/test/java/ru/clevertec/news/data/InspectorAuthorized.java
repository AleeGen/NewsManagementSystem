package ru.clevertec.news.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import ru.clevertec.news.dto.auth.AuthDto;

import java.util.List;

@UtilityClass
public class InspectorAuthorized {

    private static final ObjectMapper mapper = new ObjectMapper();
    public static final String TOKEN = "Bearer ...";

    @SneakyThrows
    public static String invalidRoleForSubscriber() {
        return mapper.writeValueAsString(new AuthDto("username-30", List.of("JOURNALIST")));
    }

    @SneakyThrows
    public static String validUserAndRoleForSubscriber() {
        return mapper.writeValueAsString(new AuthDto("username-30", List.of("SUBSCRIBER")));
    }

    @SneakyThrows
    public static String invalidUserForSubscriber() {
        return mapper.writeValueAsString(new AuthDto("username-99", List.of("SUBSCRIBER")));
    }

    @SneakyThrows
    public static String invalidRoleForJournalist() {
        return mapper.writeValueAsString(new AuthDto("username-1", List.of("SUBSCRIBER")));
    }

    @SneakyThrows
    public static String validUserAndRoleForJournalist() {
        return mapper.writeValueAsString(new AuthDto("username-1", List.of("JOURNALIST")));
    }

    @SneakyThrows
    public static String invalidUserForJournalist() {
        return mapper.writeValueAsString(new AuthDto("username-99", List.of("JOURNALIST")));
    }

}