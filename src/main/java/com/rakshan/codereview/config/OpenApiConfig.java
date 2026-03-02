package com.rakshan.codereview.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for API documentation.
 * Configures the API title, description, and JWT Bearer authentication scheme.
 * Accessible at /swagger-ui.html when the application is running.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Decentralised Peer Code Review Platform API")
                        .description("REST API for the peer code review platform. "
                                + "Supports code submission, anonymous peer review, "
                                + "multi-dimensional scoring, and review quality prediction.")
                        .version("1.0.0"))
                // Configure JWT Bearer authentication for Swagger UI
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
