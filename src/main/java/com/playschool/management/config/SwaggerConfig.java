package com.playschool.management.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";

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
                        .termsOfService("https://www.jrtransport.com/terms"))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, 
                            new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter JWT Bearer token **_only_**")))
                .addSecurityItem(new SecurityRequirement()
                        .addList(SECURITY_SCHEME_NAME));
    }
}
