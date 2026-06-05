package rw.utility.utility_billing_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import rw.utility.utility_billing_system.util.ValidationPatterns;

/**
 * Request body for creating or updating a utility customer.
 */
public record CustomerRequest(
        @NotBlank(message = "Full name is required")
        String fullName,

        @NotBlank(message = "National ID is required")
        @Size(min = 18, max = 18, message = ValidationPatterns.NATIONAL_ID_MESSAGE)
        String nationalId,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = ValidationPatterns.PHONE_NUMBER, message = ValidationPatterns.PHONE_NUMBER_MESSAGE)
        String phoneNumber,

        @NotBlank(message = "Address is required")
        String address
) {
}
