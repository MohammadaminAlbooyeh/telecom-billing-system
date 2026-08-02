package com.tbs.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI telecomBillingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Telecom Billing System API")
                        .description("REST API for managing subscribers, plans, usage (CDR) ingestion, "
                                + "rating, balances and invoice generation.")
                        .version("v0.0.1")
                        .contact(new Contact().name("Telecom Billing System Team")));
    }
}
