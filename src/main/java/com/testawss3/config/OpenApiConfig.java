package com.testawss3.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("testAwsS3 API")
                        .version("1.0")
                        .description("REST thao tác file trên Amazon S3 (upload, download, list, xóa)."));
    }
}
