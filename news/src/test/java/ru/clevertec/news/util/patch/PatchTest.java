package ru.clevertec.news.util.patch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Nested;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import ru.clevertec.exceptionhandlerstarter.exception.impl.PatchException;
import ru.clevertec.news.data.builder.impl.dto.patch.PatchReqBuilder;
import ru.clevertec.news.data.reader.PatchReader;
import ru.clevertec.news.entity.Comment;
import org.junit.jupiter.api.Test;
import ru.clevertec.news.entity.News;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PatchTest {

    @Nested
    class CheckExecuteException {

        @Test
        void shouldThrowPatchExceptionWithNotFoundField() {
            String nonExistField = "nonExistField";
            var patch = PatchReqBuilder.patchReq()
                    .withField(nonExistField)
                    .withValue("newValue").build();
            var exception = assertThrows(PatchException.class, () -> Patch.execute(patch, Comment.class));
            assertThat(exception.getMessage()).isEqualTo("Element 'nonExistField' not found.");
        }

        @Test
        void shouldThrowPatchExceptionWithInvalidJson() {
            String badJson = "newText";
            var patch = PatchReqBuilder.patchReq()
                    .withField("text")
                    .withValue(badJson).build();
            var exception = assertThrows(PatchException.class, () -> Patch.execute(patch, Comment.class));
            assertThat(exception.getMessage()).isEqualTo(String.format("Invalid json: %s", patch.value()));
        }

    }

    @Nested
    class CheckExecuteReturned {

        private final ObjectMapper mapper = new Jackson2ObjectMapperBuilder().deserializerByType(LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss:SSS"))).build();

        @Test
        @SneakyThrows
        void checkNotCollectionFieldShouldReturnExpected() {
            Class<?> clazz = Comment.class;
            String modifiedField = "text";
            var expectedField = clazz.getDeclaredField(modifiedField);
            String expectedValue = "newText";
            var patch = PatchReqBuilder.patchReq()
                    .withField(modifiedField)
                    .withValue(String.format("\"%s\"", expectedValue)).build();
            var actual = Patch.execute(patch, clazz);
            assertAll(
                    () -> assertThat(actual.modifiedField()).isEqualTo(expectedField),
                    () -> assertThat(actual.value()).isEqualTo(expectedValue));
        }

        @Test
        @SneakyThrows
        void checkCollectionFieldShouldReturnExpected() {
            Class<?> clazz = News.class;
            String modifiedField = "comments";
            var expectedField = clazz.getDeclaredField(modifiedField);
            var expectedValue = PatchReader.getList();
            var patch = PatchReqBuilder.patchReq()
                    .withField(modifiedField)
                    .withValue(mapper.writeValueAsString(expectedValue)).build();
            var actual = Patch.execute(patch, clazz);
            assertAll(
                    () -> assertThat(actual.modifiedField()).isEqualTo(expectedField),
                    () -> assertThat(actual.value()).isEqualTo(expectedValue));
        }

    }

}