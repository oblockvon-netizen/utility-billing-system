package rw.utility.utility_billing_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request body for creating or updating a utility customer.
 */

public record CustomerRequest(
        @NotBlank(message = "Full name is required")
        String fullName,

        @NotBlank(message = "National ID is required")
        // Unique identifier enforced at the service layer
        String nationalId,
        @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Phone number is required")
        String phoneNumber,

        @NotBlank(message = "Address is required")
        String address
) {
}
