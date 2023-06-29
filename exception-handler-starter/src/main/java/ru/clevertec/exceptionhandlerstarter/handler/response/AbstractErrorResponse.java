package ru.clevertec.exceptionhandlerstarter.handler.response;

import lombok.Data;

@Data
public abstract class AbstractErrorResponse {

    private String message;
    private int status;

}