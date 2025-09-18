package org.ps.dummy.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Dummy Users API").version("1.0").description("API to search and retrieve users fetched from an external service"))
                .externalDocs(new ExternalDocumentation().description("Dummy API docs").url("https://dummyjson.com/"));
    }
}

