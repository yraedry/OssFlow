package com.ossflow.shared.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ossflowOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OssFlow API")
                        .description("Segundo cerebro para BJJ — API REST")
                        .version("v1"))
                .components(new Components().addHeaders("X-Trace-Id",
                        new Header().description("Trace ID propagado end-to-end")
                                .schema(new StringSchema())));
    }
}
