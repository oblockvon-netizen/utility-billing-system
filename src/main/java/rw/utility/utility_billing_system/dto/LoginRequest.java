package rw.utility.utility_billing_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Credentials payload for JWT authentication.
 */

public record LoginRequest(
        @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
        // Matched case-insensitively against stored user email
        String email,
        @NotBlank(message = "Password is required")
        String password
) {
}
