package ru.clevertec.loggingstarter.data;

import ru.clevertec.loggingstarter.annotation.Logging;

public class OnMethodLogging {

    @Logging
    public String method(String str) {
        return str;
    }

}