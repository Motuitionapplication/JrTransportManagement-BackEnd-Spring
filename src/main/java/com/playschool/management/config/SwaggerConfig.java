package com.playschool.management.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                    new Server().url("http://localhost:8080").description("Development Server"),
                    new Server().url("https://your-production-domain.com").description("Production Server")
                ))
                .info(new Info()
                        .title("Jr Transport Management API")
                        .description("REST API for Junior Transport Management System - A comprehensive solution for managing vehicles, owners, drivers, customers, and bookings in the transport industry.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Jr Transport Management Team")
                                .email("support@jrtransport.com")
                                .url("https://www.jrtransport.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT"))
                        .termsOfService("https://www.jrtransport.com/terms"));
    }
}
