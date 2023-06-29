package ru.clevertec.exceptionhandlerstarter.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "ru.clevertec.exception-handler")
public class ExceptionHandlerProperties {

    /**
     * to enabled exception handler
     */
    private boolean enabled;

}