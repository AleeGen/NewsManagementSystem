package ru.clevertec.exceptionhandlerstarter.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.clevertec.exceptionhandlerstarter.exception.AbstractException;
import ru.clevertec.exceptionhandlerstarter.exception.impl.CommunicationException;
import ru.clevertec.exceptionhandlerstarter.handler.response.AbstractErrorResponse;
import ru.clevertec.exceptionhandlerstarter.handler.response.impl.BanResponse;
import ru.clevertec.exceptionhandlerstarter.handler.response.impl.CommunicationResponse;
import ru.clevertec.exceptionhandlerstarter.handler.response.impl.ExceptionResponse;
import ru.clevertec.exceptionhandlerstarter.handler.response.impl.NotFoundElementResponse;
import ru.clevertec.exceptionhandlerstarter.handler.response.impl.PatchResponse;
import ru.clevertec.exceptionhandlerstarter.exception.impl.BanException;
import ru.clevertec.exceptionhandlerstarter.exception.impl.NotFoundElementException;
import ru.clevertec.exceptionhandlerstarter.exception.impl.PatchException;
import ru.clevertec.exceptionhandlerstarter.handler.response.impl.ThrowableResponse;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler
    public ResponseEntity<NotFoundElementResponse> handleException(NotFoundElementException e) {
        return getResponse(new NotFoundElementResponse(), e.getCode(), e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<PatchResponse> handleException(PatchException e) {
        return getResponse(new PatchResponse(), e.getCode(), e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<BanResponse> handleException(BanException e) {
        return getResponse(new BanResponse(), e.getCode(), e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<CommunicationResponse> handleException(CommunicationException e) {
        return getResponse(new CommunicationResponse(), e.getCode(), e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleException(AbstractException e) {
        return getResponse(new ExceptionResponse(), e.getCode(), e.getMessage());
    }

    private <T extends AbstractErrorResponse> ResponseEntity<T> getResponse(T error, int status, String message) {
        error.setStatus(status);
        error.setMessage(message);
        return new ResponseEntity<>(error, HttpStatus.valueOf(status));
    }

}