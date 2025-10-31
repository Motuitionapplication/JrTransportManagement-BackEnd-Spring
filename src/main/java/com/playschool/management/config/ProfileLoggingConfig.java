package com.playschool.management.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

@Configuration
public class ProfileLoggingConfig {

    private static final Logger log = LoggerFactory.getLogger(ProfileLoggingConfig.class);

    @Bean
    @Profile("dev")
    ApplicationListener<ApplicationReadyEvent> devDashboardLogging(Environment environment) {
        return event -> {
            String port = environment.getProperty("local.server.port",
                    environment.getProperty("server.port", "8080"));
            log.info("Driver dashboard backend ready in DEV profile on port {}", port);
        };
    }

    @Bean
    @Profile("prod")
    ApplicationListener<ApplicationReadyEvent> prodDashboardLogging(Environment environment) {
        return event -> {
            String port = environment.getProperty("local.server.port",
                    environment.getProperty("server.port", "8080"));
            log.info("Driver dashboard backend ready in PROD profile on port {}", port);
        };
    }
}
