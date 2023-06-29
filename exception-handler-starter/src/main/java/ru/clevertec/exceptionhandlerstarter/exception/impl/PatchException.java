package ru.clevertec.exceptionhandlerstarter.exception.impl;

import ru.clevertec.exceptionhandlerstarter.exception.AbstractException;

public class PatchException extends AbstractException {

    public PatchException(String message, int code) {
        super(message, code);
    }

}