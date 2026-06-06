package rw.utility.utility_billing_system.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "WASAC Utility Billing System API",
                version = "1.0.0",
                description = "Backend API for water and electricity billing management"
        )
)
// Enables Authorize in Swagger UI for protected endpoints
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
/**
 * OpenAPI metadata and JWT bearer scheme for Swagger UI authentication.
 */
public class SwaggerConfig {
}
