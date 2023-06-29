package ru.clevertec.loggingstarter.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "ru.clevertec.logging")
public class LoggingProperties {

    /**
     * to enable logging
     */
    private boolean enabled;

}