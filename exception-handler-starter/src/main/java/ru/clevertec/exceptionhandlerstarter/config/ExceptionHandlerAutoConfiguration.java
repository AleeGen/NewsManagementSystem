package ru.clevertec.exceptionhandlerstarter.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.clevertec.exceptionhandlerstarter.handler.ExceptionHandlerController;

@Configuration
@ConditionalOnClass(ExceptionHandlerProperties.class)
@EnableConfigurationProperties(ExceptionHandlerProperties.class)
@ConditionalOnProperty(prefix = "ru.clevertec.exception-handler", name = "enabled", havingValue = "true")
public class ExceptionHandlerAutoConfiguration {

    @Bean
    public ExceptionHandlerController exceptionHandlerController() {
        return new ExceptionHandlerController();
    }

}