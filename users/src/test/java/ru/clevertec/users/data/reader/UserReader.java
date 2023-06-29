package ru.clevertec.users.data.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import ru.clevertec.users.dto.users.UserResponse;
import ru.clevertec.users.entity.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@UtilityClass
public class UserReader {

    private final ObjectMapper mapper = new ObjectMapper();

    public static List<User> findAllUserDefault() throws IOException {
        InputStream inputStream = ClassLoader
                .getSystemResourceAsStream("data/user_expected/find_all/user-default-pageable.json");
        return mapper.readValue(inputStream, new TypeReference<>() {
        });
    }

    public static List<UserResponse> findAllWithParametersPageable() throws IOException {
        return getList("find_all/page-1-size-2.json");
    }

    public static List<UserResponse> findAllUserResponseDefault() throws IOException {
        return getList("find_all/user-response-default-pageable.json");
    }

    public static List<UserResponse> findAllWithFilter() throws IOException {
        return getList("find_all/filter-by-firstname.json");
    }

    public static UserResponse findById(Long id) throws IOException {
        return get(String.format("find_by_id/%d.json", id));
    }

    private static List<UserResponse> getList(String path) throws IOException {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("data/user_expected/" + path);
        return mapper.readValue(inputStream, new TypeReference<>() {
        });
    }

    private static UserResponse get(String path) throws IOException {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("data/user_expected/" + path);
        return mapper.readValue(inputStream, UserResponse.class);
    }

}