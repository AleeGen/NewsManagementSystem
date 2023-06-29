package ru.clevertec.news.util.patch;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.ReflectionUtils;
import ru.clevertec.exceptionhandlerstarter.exception.impl.PatchException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Objects;

/**
 * Utility class for generating {@link PatchResponse} by {@link PatchRequest}.
 */
@UtilityClass
public class Patch {

    private static final ObjectMapper mapper;

    static {
        mapper = new Jackson2ObjectMapperBuilder().deserializerByType(LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss:SSS"))).build();
    }

    /**
     * Method for generating {@link PatchResponse} by {@link PatchRequest} and {@link Class}
     *
     * @param patch incoming update {@link PatchResponse}
     * @param clazz updated class
     * @return {@link PatchRequest}
     * @throws PatchException if the specified field does not exist or the value does not match the json format
     */
    public static PatchResponse execute(PatchRequest patch, Class<?> clazz) {
        Field field = ReflectionUtils.findField(clazz, patch.field());
        if (Objects.isNull(field)) {
            throw new PatchException(
                    String.format("Element '%s' not found.", patch.field()),
                    HttpStatus.NOT_ACCEPTABLE.value());
        }
        String valueStr = patch.value();
        try {
            Object value = Collection.class.isAssignableFrom(field.getType()) ?
                    mapper.readValue(valueStr, mapper.getTypeFactory().constructCollectionType(Collection.class,
                            (Class<?>) ((ParameterizedType) (field.getGenericType())).getActualTypeArguments()[0])) :
                    mapper.readValue(valueStr, field.getType());
            return new PatchResponse(field, value);
        } catch (JacksonException eJson) {
            throw new PatchException(
                    String.format("Invalid json: %s", patch.value()),
                    HttpStatus.BAD_REQUEST.value());
        }
    }

}