package ru.clevertec.news.data.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.experimental.UtilityClass;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import ru.clevertec.news.dto.comment.CommentResponse;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@UtilityClass
public class CommentReader {

    private final ObjectMapper mapper = new Jackson2ObjectMapperBuilder().deserializerByType(LocalDateTime.class,
            new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss:SSS"))).build();

    public static List<CommentResponse> findAllWithParametersPageable() throws IOException {
        return getList("find_all/page-20-size-5.json");
    }

    public static List<CommentResponse> findAllDefaultPageable() throws IOException {
        return getList("find_all/default-pageable.json");
    }

    public static List<CommentResponse> findAllWithFilter() throws IOException {
        return getList("find_all/filter-by-username-sort-by-time.json");
    }

    public static CommentResponse findById(Long id) throws IOException {
        return get(String.format("find_by_id/%d.json", id));
    }

    private static List<CommentResponse> getList(String path) throws IOException {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("data/comment_expected/" + path);
        return mapper.readValue(inputStream, new TypeReference<>() {
        });
    }

    private static CommentResponse get(String path) throws IOException {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("data/comment_expected/" + path);
        return mapper.readValue(inputStream, CommentResponse.class);
    }

}