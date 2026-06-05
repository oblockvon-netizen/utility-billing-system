package rw.utility.utility_billing_system.dto;

import jakarta.validation.constraints.NotNull;
import rw.utility.utility_billing_system.enums.Status;

public record StatusUpdateRequest(
        @NotNull(message = "Status is required")
        Status status
) {
}
