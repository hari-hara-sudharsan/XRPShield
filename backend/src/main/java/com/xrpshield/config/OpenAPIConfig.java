package com.xrpshield.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("XRPShield Core API Architecture")
                        .version("1.0.0")
                        .description("Production-grade API endpoints for XRP Treasury & Privacy Risk Engine on Flare Network")
                        .contact(new Contact()
                                .name("XRPShield Engineering Team")
                                .url("https://xrpshield.io"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
