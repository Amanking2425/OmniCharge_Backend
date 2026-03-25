package com.omincharge.operator;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI operatorServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Operator Service API")
                        .description("OmniCharge — Telecom operators and recharge plans management")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("OmniCharge Team")
                                .email("support@omnicharge.com")));
    }
}