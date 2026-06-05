package rw.utility.utility_billing_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import rw.utility.utility_billing_system.enums.Role;

public record AssignRoleRequest(
        @NotNull(message = "Role is required")
        @Schema(
                description = "Role to assign to the user",
                example = "ROLE_FINANCE",
                allowableValues = {"ROLE_ADMIN", "ROLE_OPERATOR", "ROLE_FINANCE", "ROLE_CUSTOMER"}
        )
        Role role
) {
}
