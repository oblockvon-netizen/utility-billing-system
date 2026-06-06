package rw.utility.utility_billing_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import rw.utility.utility_billing_system.enums.Role;

/**
 * Public registration payload; defaults to ROLE_CUSTOMER when role is omitted.
 */

public record SignupRequest(
        @NotBlank(message = "Full name is required")
        String fullName,

        @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Phone number is required")
        String phoneNumber,

        @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must contain at least 8 characters")
        String password,

        @Schema(
                description = "Optional. Defaults to ROLE_CUSTOMER. Public signup only permits ROLE_CUSTOMER.",
                example = "ROLE_CUSTOMER",
                defaultValue = "ROLE_CUSTOMER"
        )
        // Optional; non-customer roles are rejected at signup
        Role role) {
}
