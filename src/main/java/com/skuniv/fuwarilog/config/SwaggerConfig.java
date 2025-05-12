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
        //String securityJwtName = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("BearerToken");
        Components components = new Components().addSecuritySchemes("BearerToken",
                new SecurityScheme().name("BearerToken")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme(BEARER_TOKEN_PREFIZ)
                        .bearerFormat("BearerToken"));

        return new OpenAPI().addSecurityItem(securityRequirement).components(components);
    }
}
