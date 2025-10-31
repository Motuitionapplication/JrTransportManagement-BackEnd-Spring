package com.playschool.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

@Configuration
public class CorsConfig {

    private static final String[] ALLOWED_ORIGINS = {
        "http://localhost:4200",
        "http://localhost:3000",
        "https://playschool-a2z.netlify.app",
        "http://127.0.0.1:5500",
        "http://localhost:5500",
        "https://your-frontend-domain.com"
    };

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {

                // Application APIs
                registry.addMapping("/api/**")
                        .allowedOrigins(ALLOWED_ORIGINS)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(3600);
                
                registry.addMapping("/ws/**")
                	.allowedOrigins(ALLOWED_ORIGINS)
                	.allowedMethods("GET", "POST", "OPTIONS", "PUT", "DELETE", "PATCH")
                	.allowedHeaders("*")
                	.allowCredentials(true);

                // Swagger UI
                registry.addMapping("/swagger-ui/**")
                        .allowedOrigins(ALLOWED_ORIGINS)
                        .allowedMethods("GET")
                        .allowedHeaders("*")
                        .allowCredentials(false);

                // Swagger API Docs
                registry.addMapping("/api-docs/**")
                        .allowedOrigins(ALLOWED_ORIGINS)
                        .allowedMethods("GET")
                        .allowedHeaders("*")
                        .allowCredentials(false);
            }
        };
    }
}
