//package com.omincharge.user;
//
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.info.Contact;
//import io.swagger.v3.oas.models.info.Info;
//import io.swagger.v3.oas.models.servers.Server;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.List;
//
//@Configuration
//public class SwaggerConfig {
//
//    @Bean
//    public OpenAPI userServiceOpenAPI() {
//        return new OpenAPI()
//                .servers(List.of(
//                        new Server().url("/") // ✅ FORCE GATEWAY
//                ))
//                .info(new Info()
//                        .title("User Service API")
//                        .description("OmniCharge — User registration, login and profile management")
//                        .version("1.0.0")
//                        .contact(new Contact()
//                                .name("OmniCharge Team")
//                                .email("support@omnicharge.com")));
//    }
//}

package com.omincharge.user;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SwaggerConfig {

    @Bean
    public OpenAPI userServiceOpenAPI() {
        return new OpenAPI()
                .servers(List.of(new Server().url("/")))
                .components(new Components())
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .info(new Info()
                        .title("User Service API")
                        .description("OmniCharge - User registration, login and profile management")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("OmniCharge Team")
                                .email("support@omnicharge.com")));
    }
}

