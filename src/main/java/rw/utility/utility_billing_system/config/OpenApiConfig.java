package rw.utility.utility_billing_system.config;

import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
/**
 * OpenAPI customization that documents standard HTTP error responses on every operation.
 */
public class OpenApiConfig {

    @Bean
    public OperationCustomizer documentStandardResponses() {
        return (operation, handlerMethod) -> {
            addResponse(operation, "400", "Bad Request");
            addResponse(operation, "401", "Unauthorized");
            addResponse(operation, "403", "Forbidden");
            addResponse(operation, "404", "Not Found");
            addResponse(operation, "409", "Conflict");
            addResponse(operation, "422", "Unprocessable Entity");
            addResponse(operation, "500", "Internal Server Error");
            addResponse(operation, "503", "Service Unavailable");
            return operation;
        };
    }

    private void addResponse(
            io.swagger.v3.oas.models.Operation operation,
            String statusCode,
            String description
    ) {
        // Skip statuses already declared on the controller method
        if (operation.getResponses() != null && operation.getResponses().containsKey(statusCode)) {            return;
        }
        operation.getResponses().addApiResponse(
                statusCode,
                new ApiResponse().description(description)
        );
    }
}
