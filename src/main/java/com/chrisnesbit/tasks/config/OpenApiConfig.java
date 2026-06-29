package com.chrisnesbit.tasks.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Task Tracker API",
                version = "1.0.0",
                description = "A lightweight Spring Boot REST API for managing task workflow state.",
                contact = @Contact(name = "Chris Nesbit"),
                license = @License(name = "Portfolio Demo")
        )
)
public class OpenApiConfig {
}
