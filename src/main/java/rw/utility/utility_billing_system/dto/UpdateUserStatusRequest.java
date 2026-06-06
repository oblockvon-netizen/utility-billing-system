package rw.utility.utility_billing_system.dto;

import jakarta.validation.constraints.NotNull;
import rw.utility.utility_billing_system.enums.Status;

/**
 * Admin request body for activating or deactivating a user account.
 */

public record UpdateUserStatusRequest(
        @NotNull(message = "Status is required")
        // Deactivated users cannot authenticate
        Status status) {
}
