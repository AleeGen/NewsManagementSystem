package ru.clevertec.news.data.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.experimental.UtilityClass;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import ru.clevertec.news.entity.Comment;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@UtilityClass
public class PatchReader {

    private final ObjectMapper mapper = new Jackson2ObjectMapperBuilder().deserializerByType(LocalDateTime.class,
            new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss:SSS"))).build();

    public static List<Comment> getList() throws IOException {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("data/patch_request/list-comment.json");
        return mapper.readValue(inputStream, new TypeReference<>() {
        });
    }

}