package ru.clevertec.exceptionhandlerstarter.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.clevertec.exceptionhandlerstarter.config.ExceptionHandlerAutoConfiguration;
import ru.clevertec.exceptionhandlerstarter.data.TestController;
import ru.clevertec.exceptionhandlerstarter.handler.response.impl.BanResponse;
import ru.clevertec.exceptionhandlerstarter.handler.response.impl.CommunicationResponse;
import ru.clevertec.exceptionhandlerstarter.handler.response.impl.ExceptionResponse;
import ru.clevertec.exceptionhandlerstarter.handler.response.impl.NotFoundElementResponse;
import ru.clevertec.exceptionhandlerstarter.handler.response.impl.PatchResponse;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@EnableAutoConfiguration
@SpringBootTest(classes = {ExceptionHandlerAutoConfiguration.class, TestController.class},
        properties = "ru.clevertec.exception-handler.enabled=true")
class ExceptionHandlerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void checkNotFoundException() throws Exception {
        var response = new NotFoundElementResponse();
        response.setMessage("Not found element");
        response.setStatus(404);
        mockMvc.perform(get("/test/not-found-element"))
                .andExpect(status().is(404))
                .andExpect(content().string(mapper.writeValueAsString(response)));
    }

    @Test
    void checkPatchException() throws Exception {
        var response = new PatchResponse();
        response.setMessage("Patch");
        response.setStatus(400);
        mockMvc.perform(get("/test/patch"))
                .andExpect(status().is(400))
                .andExpect(content().string(mapper.writeValueAsString(response)));
    }

    @Test
    void checkBanException() throws Exception {
        var response = new BanResponse();
        response.setMessage("Ban");
        response.setStatus(403);
        mockMvc.perform(get("/test/ban"))
                .andExpect(status().is(403))
                .andExpect(content().string(mapper.writeValueAsString(response)));
    }

    @Test
    void checkCommunicationException() throws Exception {
        var response = new CommunicationResponse();
        response.setMessage("Communication");
        response.setStatus(403);
        mockMvc.perform(get("/test/communication"))
                .andExpect(status().is(403))
                .andExpect(content().string(mapper.writeValueAsString(response)));
    }

    @Test
    void checkAbstractException() throws Exception {
        var response = new ExceptionResponse();
        response.setMessage("Abstract");
        response.setStatus(500);
        mockMvc.perform(get("/test/abstract"))
                .andExpect(status().is(500))
                .andExpect(content().string(mapper.writeValueAsString(response)));
    }

}