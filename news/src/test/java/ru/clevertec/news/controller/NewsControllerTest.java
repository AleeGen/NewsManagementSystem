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
import ru.clevertec.news.data.builder.impl.dto.news.NewsReqBuilder;
import ru.clevertec.news.data.builder.impl.dto.patch.PatchReqBuilder;
import ru.clevertec.news.data.builder.impl.dto.news.NewsResBuilder;
import ru.clevertec.news.dto.news.NewsRequest;
import ru.clevertec.news.dto.news.NewsResponse;
import ru.clevertec.news.service.NewsService;
import ru.clevertec.news.util.patch.PatchRequest;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NewsController.class)
@Import(SecurityConfig.class)
@WithMockUser(username = "username", authorities = "JOURNALIST")
@MockBeans(@MockBean(ExternalUserApi.class))
public class NewsControllerTest {

    private final String MAPPING = "/news";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean(name = "newsServiceImpl")
    private NewsService service;

    @Nested
    class CheckValidation {

        private static Stream<NewsRequest> negativeRequest() {
            StringBuilder charactersMore70 = new StringBuilder();
            StringBuilder charactersMore1000 = new StringBuilder();
            IntStream.range(0, 71).forEach(i -> charactersMore70.append("."));
            IntStream.range(0, 1001).forEach(i -> charactersMore1000.append("."));
            return Stream.of(
                    NewsReqBuilder.newsReq().withTitle(null).build(),
                    NewsReqBuilder.newsReq().withTitle("").build(),
                    NewsReqBuilder.newsReq().withTitle(" ").build(),
                    NewsReqBuilder.newsReq().withTitle(charactersMore70.toString()).build(),
                    NewsReqBuilder.newsReq().withText(null).build(),
                    NewsReqBuilder.newsReq().withText("").build(),
                    NewsReqBuilder.newsReq().withText(" ").build(),
                    NewsReqBuilder.newsReq().withText(charactersMore1000.toString()).build());
        }

        private static Stream<NewsRequest> positiveRequest() {
            return Stream.of(
                    NewsReqBuilder.newsReq().withTitle("something").build(),
                    NewsReqBuilder.newsReq().withText("something").build());
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("negativeRequest")
        void checkGetShouldReturnError(NewsRequest request) {
            mockMvc.perform(post(MAPPING)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("positiveRequest")
        void checkPostShouldAccept(NewsRequest request) {
            mockMvc.perform(post(MAPPING)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("negativeRequest")
        void checkPutShouldReturnError(NewsRequest request) {
            long id = 1L;
            mockMvc.perform(put(MAPPING + "/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("positiveRequest")
        void checkPutShouldAccept(NewsRequest request) {
            long id = 1L;
            NewsResponse newsById = NewsResBuilder.newsRes().withUsername("username").build();
            doReturn(newsById).when(service).findById(id);
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
            mockMvc.perform(patch(MAPPING)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isMethodNotAllowed());
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("positivePatch")
        void checkPatchShouldAccept(PatchRequest request) {
            long id = 1L;
            NewsResponse newsById = NewsResBuilder.newsRes().withUsername("username").build();
            doReturn(newsById).when(service).findById(id);
            mockMvc.perform(patch(MAPPING + "/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

    }

}