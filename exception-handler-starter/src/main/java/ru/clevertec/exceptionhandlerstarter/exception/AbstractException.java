package ru.clevertec.exceptionhandlerstarter.exception;

public class AbstractException extends RuntimeException {

    private final int code;

    public AbstractException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}