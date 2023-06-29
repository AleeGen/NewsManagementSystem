package ru.clevertec.cachestarter.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.clevertec.cachestarter.cache.Algorithm;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "ru.clevertec.cache")
public class CacheProperties {

    /**
     * to enable cache
     */
    private boolean enabled;

    /**
     * LFU or LRU
     */
    private Algorithm algorithm = Algorithm.LFU;

    /**
     * cache memory capacity
     */
    private int capacity = 5;

}