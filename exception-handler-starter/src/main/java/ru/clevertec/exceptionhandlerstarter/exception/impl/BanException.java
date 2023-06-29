package ru.clevertec.exceptionhandlerstarter.exception.impl;

import ru.clevertec.exceptionhandlerstarter.exception.AbstractException;

public class BanException extends AbstractException {

    public BanException(String message, int code) {
        super(message,code);
    }

}