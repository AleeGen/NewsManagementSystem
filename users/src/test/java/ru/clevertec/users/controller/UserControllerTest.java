package ru.clevertec.users.controller;

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
import ru.clevertec.users.config.SecurityConfig;
import ru.clevertec.users.data.builder.impl.dto.users.UserReqBuilder;
import ru.clevertec.users.data.builder.impl.dto.users.UserResBuilder;
import ru.clevertec.users.dto.users.UserRequest;
import ru.clevertec.users.dto.users.UserResponse;
import ru.clevertec.users.service.JwtService;
import ru.clevertec.users.service.UserService;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(UserController.class)
@WithMockUser(username = "username", authorities = "SUBSCRIBER")
@MockBeans(@MockBean(JwtService.class))
class UserControllerTest {

    private final String MAPPING = "/users";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean(name = "userServiceImpl")
    private UserService userService;

    @Nested
    class CheckValidation {

        private static Stream<UserRequest> negativeRequest() {
            StringBuilder charactersMore25 = new StringBuilder();
            IntStream.range(0, 26).forEach(i -> charactersMore25.append("."));
            return Stream.of(
                    UserReqBuilder.userReq().withFirstname(null).build(),
                    UserReqBuilder.userReq().withFirstname("").build(),
                    UserReqBuilder.userReq().withFirstname(" ").build(),
                    UserReqBuilder.userReq().withFirstname(charactersMore25.toString()).build(),
                    UserReqBuilder.userReq().withLastname(null).build(),
                    UserReqBuilder.userReq().withLastname("").build(),
                    UserReqBuilder.userReq().withLastname(" ").build(),
                    UserReqBuilder.userReq().withLastname(charactersMore25.toString()).build(),
                    UserReqBuilder.userReq().withEmail(null).build(),
                    UserReqBuilder.userReq().withEmail("").build(),
                    UserReqBuilder.userReq().withEmail(" ").build(),
                    UserReqBuilder.userReq().withEmail("email-gmail.com").build());
        }

        private static Stream<UserRequest> positiveRequest() {
            return Stream.of(
                    UserReqBuilder.userReq().withFirstname("firstname").build(),
                    UserReqBuilder.userReq().withLastname("lastname").build(),
                    UserReqBuilder.userReq().withEmail("email@gmail.com").build());
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("negativeRequest")
        void checkPutShouldReturnError(UserRequest request) {
            long id = 1L;
            UserResponse userById = UserResBuilder.userRes().withUsername("username").build();
            doReturn(userById).when(userService).findById(id);
            mockMvc.perform(put(MAPPING + "/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("positiveRequest")
        void checkPutShouldAccept(UserRequest request) {
            long id = 1L;
            UserResponse userById = UserResBuilder.userRes().withUsername("username").build();
            doReturn(userById).when(userService).findById(id);
            mockMvc.perform(put(MAPPING + "/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

    }

}