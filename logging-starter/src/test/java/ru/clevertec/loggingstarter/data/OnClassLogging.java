package ru.clevertec.loggingstarter.data;

import ru.clevertec.loggingstarter.annotation.Logging;

@Logging
public class OnClassLogging {

    public String method(String str) {
        return str;
    }

}