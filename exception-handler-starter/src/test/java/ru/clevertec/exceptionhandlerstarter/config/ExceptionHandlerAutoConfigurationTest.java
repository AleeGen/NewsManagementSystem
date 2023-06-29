package ru.clevertec.exceptionhandlerstarter.config;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import ru.clevertec.exceptionhandlerstarter.handler.ExceptionHandlerController;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionHandlerAutoConfigurationTest {

    public static final String ENABLED = "ru.clevertec.exception-handler.enabled=";
    private static WebApplicationContextRunner contextRunner;

    @BeforeAll
    static void init() {
        contextRunner = new WebApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(ExceptionHandlerAutoConfiguration.class));
    }

    @Test
    void shouldAddBeans() {
        contextRunner.withPropertyValues(ENABLED + "true")
                .run(context -> assertThat(context).hasNotFailed()
                        .hasSingleBean(ExceptionHandlerProperties.class)
                        .hasSingleBean(ExceptionHandlerController.class)
                        .getBean(ExceptionHandlerProperties.class).hasNoNullFieldsOrPropertiesExcept());
    }

    @Test
    void shouldNotAddBeans() {
        contextRunner.withPropertyValues(ENABLED + "false")
                .run(context -> assertThat(context).hasNotFailed()
                        .doesNotHaveBean(ExceptionHandlerAutoConfiguration.class)
                        .doesNotHaveBean(ExceptionHandlerProperties.class)
                        .doesNotHaveBean(ExceptionHandlerController.class));
    }

}