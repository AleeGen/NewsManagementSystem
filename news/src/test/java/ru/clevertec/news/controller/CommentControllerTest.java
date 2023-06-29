package ru.clevertec.news.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.clevertec.news.client.ExternalUserApi;
import ru.clevertec.news.config.SecurityConfig;
import ru.clevertec.news.data.builder.impl.dto.comments.CommentReqBuilder;
import ru.clevertec.news.data.builder.impl.dto.patch.PatchReqBuilder;
import ru.clevertec.news.data.builder.impl.dto.comments.CommentResBuilder;
import ru.clevertec.news.dto.comment.CommentRequest;
import ru.clevertec.news.dto.comment.CommentResponse;
import ru.clevertec.news.service.CommentService;
import ru.clevertec.news.util.patch.PatchRequest;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(CommentController.class)
@WithMockUser(username = "username", authorities = "SUBSCRIBER")
@MockBeans(@MockBean(ExternalUserApi.class))
public class CommentControllerTest {

    private final String MAPPING = "/comments";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean(name = "commentServiceImpl")
    private CommentService service;

    @Nested
    class CheckValidation {

        private static Stream<CommentRequest> negativeRequest() {
            StringBuilder charactersMore200 = new StringBuilder();
            IntStream.range(0, 201).forEach(i -> charactersMore200.append("."));
            return Stream.of(
                    CommentReqBuilder.commentReq().withUsername(null).build(),
                    CommentReqBuilder.commentReq().withUsername("").build(),
                    CommentReqBuilder.commentReq().withUsername(" ").build(),
                    CommentReqBuilder.commentReq().withText(null).build(),
                    CommentReqBuilder.commentReq().withText("").build(),
                    CommentReqBuilder.commentReq().withText(" ").build(),
                    CommentReqBuilder.commentReq().withText(charactersMore200.toString()).build(),
                    CommentReqBuilder.commentReq().withNewsId(null).build(),
                    CommentReqBuilder.commentReq().withNewsId(0L).build(),
                    CommentReqBuilder.commentReq().withNewsId(-1L).build());
        }

        private static Stream<CommentRequest> positiveRequest() {
            return Stream.of(
                    CommentReqBuilder.commentReq().withUsername("something").build(),
                    CommentReqBuilder.commentReq().withText("something").build(),
                    CommentReqBuilder.commentReq().withNewsId(1L).build());
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("negativeRequest")
        void checkPostShouldReturnError(CommentRequest request) {
            mockMvc.perform(post(MAPPING)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("positiveRequest")
        void checkPostShouldAccept(CommentRequest request) {
            mockMvc.perform(post(MAPPING)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("negativeRequest")
        void checkPutShouldReturnError(CommentRequest request) {
            mockMvc.perform(put(MAPPING + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("positiveRequest")
        void checkPutShouldAccept(CommentRequest request) {
            long id = 1L;
            CommentResponse commentById = CommentResBuilder.commentRes().withUsername("username").build();
            doReturn(commentById).when(service).findById(id);
            mockMvc.perform(put(MAPPING + "/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        private static Stream<PatchRequest> negativePatch() {
            return Stream.of(
                    PatchReqBuilder.patchReq().withField(null).build(),
                    PatchReqBuilder.patchReq().withField("").build(),
                    PatchReqBuilder.patchReq().withField(" ").build(),
                    PatchReqBuilder.patchReq().withValue(null).build());
        }

        private static Stream<PatchRequest> positivePatch() {
            return Stream.of(
                    PatchReqBuilder.patchReq().withField("something").build(),
                    PatchReqBuilder.patchReq().withValue("").build(),
                    PatchReqBuilder.patchReq().withValue(" ").build(),
                    PatchReqBuilder.patchReq().withValue("something").build());
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("negativePatch")
        void checkPatchShouldReturnError(PatchRequest request) {
            mockMvc.perform(patch(MAPPING + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("positivePatch")
        void checkPatchShouldAccept(PatchRequest request) {
            long id = 1L;
            CommentResponse commentById = CommentResBuilder.commentRes().withUsername("username").build();
            doReturn(commentById).when(service).findById(id);
            mockMvc.perform(patch(MAPPING + "/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

    }

}