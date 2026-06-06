package rw.utility.utility_billing_system.dto;

import jakarta.validation.constraints.NotNull;
import rw.utility.utility_billing_system.enums.Status;

/**
 * Generic request body for toggling ACTIVE/INACTIVE status on a resource.
 */

public record StatusUpdateRequest(
        @NotNull(message = "Status is required")
        // Shared by customer, meter, and other status endpoints
        Status status) {
}
