package ru.clevertec.exceptionhandlerstarter.exception.impl;

import ru.clevertec.exceptionhandlerstarter.exception.AbstractException;

public class NotFoundElementException extends AbstractException {

    public NotFoundElementException(String message, int code) {
        super(message, code);
    }

}