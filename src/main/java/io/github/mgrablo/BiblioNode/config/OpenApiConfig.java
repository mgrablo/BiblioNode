package io.github.mgrablo.BiblioNode.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI biblioNodeOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("BiblioNode API")
                .description("Professional Library Management System API. " +
                    "Supports book tracking, reader management, and deterministic loan logic.")
                .version("v1.0.0")
                .contact(new Contact()
                    .name("mgrablo")
                    .url("https://github.com/mgrablo/BiblioNode"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0")))
            .addServersItem(new Server().url("http://localhost:8080").description("Development Server"));
    }
}
