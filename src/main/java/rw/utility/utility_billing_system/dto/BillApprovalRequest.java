package rw.utility.utility_billing_system.dto;

import jakarta.validation.constraints.NotNull;
import rw.utility.utility_billing_system.enums.BillStatus;

public record BillApprovalRequest(
        @NotNull(message = "Status is required")
        BillStatus status
) {
}
