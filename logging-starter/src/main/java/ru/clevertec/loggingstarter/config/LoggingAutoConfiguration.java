package ru.clevertec.loggingstarter.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import ru.clevertec.loggingstarter.exploitation.LoggingAspect;
import ru.clevertec.loggingstarter.exploitation.LoggingPostProcessor;
import ru.clevertec.loggingstarter.exploitation.PointCuts;

@Configuration
@ConditionalOnClass(LoggingProperties.class)
@EnableConfigurationProperties(LoggingProperties.class)
@ConditionalOnProperty(prefix = "ru.clevertec.logging", name = "enabled", havingValue = "true")
public class LoggingAutoConfiguration {

    @Bean
    @Order(1)
    public LoggingPostProcessor loggingPostProcessor() {
        return new LoggingPostProcessor();
    }

    @Bean
    @Order(2)
    public PointCuts pointCuts() {
        return new PointCuts();
    }

    @Bean
    @Order(3)
    public LoggingAspect loggingAspect() {
        return new LoggingAspect(loggingPostProcessor().getLogMap());
    }

}