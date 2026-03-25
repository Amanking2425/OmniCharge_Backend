package com.omincharge.payment;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI paymentServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Payment Service API")
                        .description("OmniCharge — Payment processing and transaction records")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("OmniCharge Team")
                                .email("support@omnicharge.com")));
    }
}