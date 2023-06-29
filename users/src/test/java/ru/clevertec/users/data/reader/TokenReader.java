package ru.clevertec.users.data.reader;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.util.Objects;

@UtilityClass
public class TokenReader {

    public static final String PREFIX = "Bearer ";

    public static String invalidBySyntaxWithPrefix() {
        return PREFIX + invalidBySyntax();
    }

    public static String invalidByTimeWithPrefix() {
        return PREFIX + invalidByTime();
    }

    public static String validWithPrefix() {
        return PREFIX + valid();
    }

    public static String invalidByUserWithPrefix() {
        return PREFIX + invalidByUser();
    }

    public static String journalistsTokenWithPrefix() {
        return PREFIX + journalistsToken();
    }

    public static String describersTokenWithPrefix() {
        return PREFIX + describersToken();
    }

    private static String describersToken() {
        return read("dima-subscriber");
    }

    private static String journalistsToken() {
        return read("maks-journalist");
    }

    public static String invalidBySyntax() {
        return read("invalid-by-syntax");
    }

    public static String invalidByTime() {
        return read("invalid-by-time");
    }

    public static String invalidByUser() {
        return read("invalid-by-user");
    }

    public static String nonExistUserWithPrefix() {
        return PREFIX + read("non-exist-user-in-bd");
    }

    public static String valid() {
        return read("valid-admin");
    }

    @SneakyThrows
    private static String read(String path) {
        return new String(Objects.requireNonNull(ClassLoader.
                        getSystemResourceAsStream("data/token/" + path))
                .readAllBytes());
    }

}