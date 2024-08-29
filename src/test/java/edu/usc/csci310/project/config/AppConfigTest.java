package edu.usc.csci310.project.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class AppConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @Test
    void testRestTemplateBean() {
        this.contextRunner
                .withUserConfiguration(AppConfig.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(RestTemplate.class);
                    assertThat(context.getBean(RestTemplate.class)).isExactlyInstanceOf(RestTemplate.class);
                });
    }
}