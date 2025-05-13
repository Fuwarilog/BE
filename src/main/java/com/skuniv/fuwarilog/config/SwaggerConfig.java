package com.skuniv.fuwarilog.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(info = @Info(title = "Fuwarilog API Docs", description = "Swagger API 문서", version = "v1"))
@Configuration
public class SwaggerConfig {

    private static final String BEARER_TOKEN_PREFIZ = "Bearer";

    @Bean
    public OpenAPI openAPI() {
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(BEARER_TOKEN_PREFIZ);
        Components components = new Components().addSecuritySchemes(BEARER_TOKEN_PREFIZ,
                new SecurityScheme().name(BEARER_TOKEN_PREFIZ)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        return new OpenAPI().addSecurityItem(securityRequirement).components(components);
    }
}
