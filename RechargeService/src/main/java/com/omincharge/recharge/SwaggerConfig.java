package com.omincharge.recharge;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI rechargeServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Recharge Service API")
                        .description("OmniCharge — Mobile recharge initiation and tracking")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("OmniCharge Team")
                                .email("support@omnicharge.com")));
    }
}