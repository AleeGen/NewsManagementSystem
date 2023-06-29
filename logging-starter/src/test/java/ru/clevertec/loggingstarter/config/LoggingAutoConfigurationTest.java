package ru.clevertec.loggingstarter.config;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import ru.clevertec.loggingstarter.exploitation.LoggingAspect;
import ru.clevertec.loggingstarter.exploitation.LoggingPostProcessor;
import ru.clevertec.loggingstarter.exploitation.PointCuts;

import static org.assertj.core.api.Assertions.assertThat;

public class LoggingAutoConfigurationTest {

    private static final String ENABLED = "ru.clevertec.logging.enabled=";
    private static WebApplicationContextRunner contextRunner;

    @BeforeAll
    static void init() {
        contextRunner = new WebApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(LoggingAutoConfiguration.class));
    }

    @Test
    void shouldAddBeans() {
        contextRunner.withPropertyValues(ENABLED + "true")
                .run(context -> assertThat(context).hasNotFailed()
                        .hasSingleBean(LoggingProperties.class)
                        .hasSingleBean(LoggingPostProcessor.class)
                        .hasSingleBean(PointCuts.class)
                        .hasSingleBean(LoggingAspect.class)
                        .getBean(LoggingProperties.class).hasNoNullFieldsOrPropertiesExcept());
    }

    @Test
    void shouldNotAddBeans() {
        contextRunner.withPropertyValues(ENABLED + "false")
                .run(context -> assertThat(context).hasNotFailed()
                        .doesNotHaveBean(LoggingAutoConfiguration.class)
                        .doesNotHaveBean(LoggingProperties.class)
                        .doesNotHaveBean(LoggingPostProcessor.class)
                        .doesNotHaveBean(PointCuts.class)
                        .doesNotHaveBean(LoggingAspect.class));
    }

}