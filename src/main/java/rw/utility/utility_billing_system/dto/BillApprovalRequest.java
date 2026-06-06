package rw.utility.utility_billing_system.dto;

import jakarta.validation.constraints.NotNull;
import rw.utility.utility_billing_system.enums.BillStatus;

/**
 * Request body for finance/admin approval of a pending bill.
 */

public record BillApprovalRequest(
        @NotNull(message = "Status is required")
        // Only APPROVED is accepted by the approval endpoint
        BillStatus status) {
}
