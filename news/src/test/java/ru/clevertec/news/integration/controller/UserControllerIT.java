package ru.clevertec.news.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.clevertec.news.client.ExternalUserApi;
import ru.clevertec.news.config.SecurityConfig;
import ru.clevertec.news.controller.UserController;
import ru.clevertec.news.data.builder.impl.dto.users.UserReqBuilder;
import ru.clevertec.news.data.builder.impl.dto.users.UserResBuilder;
import ru.clevertec.news.dto.auth.AuthDto;
import ru.clevertec.news.dto.users.UserFilter;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.clevertec.news.data.InspectorAuthorized.TOKEN;

@Import(SecurityConfig.class)
@WebMvcTest(UserController.class)
class UserControllerIT {

    private static AuthDto authDto;
    private final String MAPPING = "/users";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ExternalUserApi externalUserApi;

    @BeforeAll
    static void init() {
        authDto = new AuthDto("username", List.of("SUBSCRIBER"));
    }

    @Test
    @SneakyThrows
    void checkFindAllShouldReturnExpected() {
        var response = ResponseEntity.ok(List.of(UserResBuilder.userRes().build()));
        String expectedBody = mapper.writeValueAsString(response.getBody());
        int expectedStatus = response.getStatusCode().value();
        var externalAuthDto = ResponseEntity.ok(authDto);
        doReturn(externalAuthDto).when(externalUserApi).getUserDetails(TOKEN);
        doReturn(response).when(externalUserApi).getAll(any(String.class), any(UserFilter.class), any(Pageable.class));
        mockMvc.perform(get(MAPPING)
                        .header(HttpHeaders.AUTHORIZATION, TOKEN))
                .andExpect(status().is(expectedStatus))
                .andExpect(content().string(expectedBody));
    }

    @Test
    @SneakyThrows
    void checkGetShouldReturnExpected() {
        var response = ResponseEntity.ok(UserResBuilder.userRes().build());
        String expectedBody = mapper.writeValueAsString(response.getBody());
        int expectedStatus = response.getStatusCode().value();
        var externalAuthDto = ResponseEntity.ok(authDto);
        doReturn(externalAuthDto).when(externalUserApi).getUserDetails(TOKEN);
        doReturn(response).when(externalUserApi).get(any(), any());
        mockMvc.perform(get(MAPPING + "/1")
                        .header(HttpHeaders.AUTHORIZATION, TOKEN))
                .andExpect(status().is(expectedStatus))
                .andExpect(content().string(expectedBody));
    }

    @Test
    @SneakyThrows
    void checkPutShouldReturnExpected() {
        var request = UserReqBuilder.userReq().build();
        var response = ResponseEntity.ok(UserResBuilder.userRes()
                .withEmail(request.email())
                .withFirstname(request.firstname())
                .withLastname(request.lastname())
                .build());
        var externalAuthDto = ResponseEntity.ok(authDto);
        doReturn(externalAuthDto).when(externalUserApi).getUserDetails(TOKEN);
        doReturn(response).when(externalUserApi).put(any(), any(), any());
        mockMvc.perform(put(MAPPING + "/1")
                        .header(HttpHeaders.AUTHORIZATION, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().is(response.getStatusCode().value()))
                .andExpect(content().string(mapper.writeValueAsString(response.getBody())));
    }

    @Test
    @SneakyThrows
    void checkDeleteShouldReturnExpected() {
        var externalAuthDto = ResponseEntity.ok(authDto);
        doReturn(externalAuthDto).when(externalUserApi).getUserDetails(TOKEN);
        doReturn(ResponseEntity.noContent().build()).when(externalUserApi).delete(any(), any());
        mockMvc.perform(delete(MAPPING + "/1")
                        .header(HttpHeaders.AUTHORIZATION, TOKEN))
                .andExpect(status().isNoContent());
    }

}