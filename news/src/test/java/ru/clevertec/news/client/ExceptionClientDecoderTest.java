package ru.clevertec.news.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.exceptionhandlerstarter.exception.impl.CommunicationException;
import ru.clevertec.exceptionhandlerstarter.handler.response.impl.CommunicationResponse;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class ExceptionClientDecoderTest {

    @InjectMocks
    private ExceptionClientDecoder decoder;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private Response response;

    @Mock
    private Response.Body body;

    @Test
    @SneakyThrows
    void checkDecodeShouldReturnIOException() {
        String expectedMessage = "exception";
        doReturn(body).when(response).body();
        doThrow(new IOException(expectedMessage)).when(body).asInputStream();
        Exception actual = decoder.decode(null, response);
        assertAll(
                () -> assertThat(actual.getClass()).isEqualTo(IOException.class),
                () -> assertThat(actual.getMessage()).isEqualTo(expectedMessage));
    }

    @Test
    @SneakyThrows
    void checkDecodeShouldReturnCommunicationException() {
        String expectedMessage = "exception";
        InputStream inputStream = InputStream.nullInputStream();
        doReturn(body).when(response).body();
        doReturn(inputStream).when(body).asInputStream();
        var communicationResponse = new CommunicationResponse();
        communicationResponse.setMessage(expectedMessage);
        communicationResponse.setStatus(400);
        doReturn(communicationResponse).when(mapper).readValue(inputStream, CommunicationResponse.class);
        Exception actual = decoder.decode(null, response);
        assertAll(
                () -> assertThat(actual.getClass()).isEqualTo(CommunicationException.class),
                () -> assertThat(actual.getMessage()).isEqualTo(expectedMessage));
    }

}