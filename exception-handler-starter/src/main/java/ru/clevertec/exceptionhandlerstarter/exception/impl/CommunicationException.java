package ru.clevertec.exceptionhandlerstarter.exception.impl;

import ru.clevertec.exceptionhandlerstarter.exception.AbstractException;

public class CommunicationException extends AbstractException {

    public CommunicationException(String message, int code) {
        super(message, code);
    }

}