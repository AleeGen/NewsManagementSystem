package ru.clevertec.cachestarter.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.clevertec.cachestarter.exploitation.CacheFactory;
import ru.clevertec.cachestarter.exploitation.CacheManager;
import ru.clevertec.cachestarter.exploitation.CachePostProcessor;

@Configuration
@RequiredArgsConstructor
@ConditionalOnClass(CacheProperties.class)
@EnableConfigurationProperties(CacheProperties.class)
@ConditionalOnProperty(prefix = "ru.clevertec.cache", name = "enabled", havingValue = "true")
public class CacheAutoConfiguration {

    private final CacheProperties properties;

    @Bean
    public CacheManager cacheManager() {
        return new CacheManager(CacheFactory.getCache(
                properties.getAlgorithm(),
                properties.getCapacity()));
    }

    @Bean
    public CachePostProcessor cachePostProcessor() {
        return new CachePostProcessor(cacheManager());
    }

}