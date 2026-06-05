package rw.utility.utility_billing_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import rw.utility.utility_billing_system.enums.Role;

import java.util.Set;

public record SetUserRolesRequest(
        @NotEmpty(message = "At least one role is required")
        @Schema(
                description = "Roles to assign to the user. Replaces any existing roles.",
                example = "[\"ROLE_FINANCE\"]",
                allowableValues = {"ROLE_ADMIN", "ROLE_OPERATOR", "ROLE_FINANCE", "ROLE_CUSTOMER"}
        )
        Set<Role> roles
) {
}
