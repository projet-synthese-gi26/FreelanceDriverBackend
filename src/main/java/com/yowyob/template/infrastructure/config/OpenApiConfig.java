package com.yowyob.template.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server; // Import ajouté
import org.springframework.beans.factory.annotation.Value; // Import ajouté
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List; // Import ajouté

@Configuration
public class OpenApiConfig {

    // Injection de l'URL définie dans le YAML
    @Value("${app.swagger.server-url}")
    private String serverUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("YowYob Microservice Template API")
                        .version("1.0.0")
                        .description("Documentation de l'API Réactive Hexagonale.")
                        .contact(new Contact()
                                .name("Équipe Backend")
                                .email("dev@yowyob.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                // Configuration des serveurs pour le mode interactif Swagger
                .servers(List.of(
                        new Server().url(serverUrl).description("Serveur de Production (Proxy)"),
                        new Server().url("/").description("Serveur par défaut (Relatif)"),
                        new Server().url("http://localhost:8081").description("Localhost")
                ))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}