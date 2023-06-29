package ru.clevertec.cachestarter.config;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import ru.clevertec.cachestarter.exploitation.CacheManager;
import ru.clevertec.cachestarter.exploitation.CachePostProcessor;

import static org.assertj.core.api.Assertions.assertThat;

public class CacheAutoConfigurationTest {

    private static WebApplicationContextRunner contextRunner;
    private final String ENABLED = "ru.clevertec.cache.enabled=";

    @BeforeAll
    static void init() {
        contextRunner = new WebApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(CacheAutoConfiguration.class));
    }

    @Test
    void shouldAddBeans() {
        contextRunner.withPropertyValues(ENABLED + "true")
                .run(context -> assertThat(context).hasNotFailed()
                        .hasSingleBean(CacheProperties.class)
                        .hasSingleBean(CacheAutoConfiguration.class)
                        .hasSingleBean(CacheManager.class)
                        .hasSingleBean(CachePostProcessor.class)
                        .getBean(CacheProperties.class).hasNoNullFieldsOrPropertiesExcept());
    }

    @Test
    void shouldNotAddBeans() {
        contextRunner.withPropertyValues(ENABLED + "false")
                .run(context -> assertThat(context).hasNotFailed()
                        .doesNotHaveBean(CacheProperties.class)
                        .doesNotHaveBean(CacheManager.class)
                        .doesNotHaveBean(CachePostProcessor.class));
    }

}