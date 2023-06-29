package ru.clevertec.news.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.clevertec.exceptionhandlerstarter.exception.impl.CommunicationException;
import ru.clevertec.exceptionhandlerstarter.handler.response.impl.CommunicationResponse;

import java.io.IOException;
import java.io.InputStream;

/**
 * A class for decoding the server response
 */
@Component
@RequiredArgsConstructor
public class ExceptionClientDecoder implements ErrorDecoder {

    private final ObjectMapper mapper;

    /**
     * Decodes the server response and returns a CommunicationException type exception in case of a communication error.
     *
     * @param methodKey method key
     * @param response  server response
     * @return exception to the type CommunicationException in case of a communication error,
     * or IOException in case of an error reading data from the server response
     */
    @Override
    public Exception decode(String methodKey, Response response) {
        try (InputStream bodyIs = response.body().asInputStream()) {
            CommunicationResponse message = mapper.readValue(bodyIs, CommunicationResponse.class);
            return new CommunicationException(message.getMessage(), message.getStatus());
        } catch (IOException e) {
            return new IOException(e.getMessage());
        }
    }

}