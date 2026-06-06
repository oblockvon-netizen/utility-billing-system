package rw.utility.utility_billing_system.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import rw.utility.utility_billing_system.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Request body for recording a payment against an approved bill.
 */

public record PaymentRequest(
        @NotNull(message = "Bill ID is required")
        // Target bill must not already be fully paid
        UUID billId,
        @NotNull(message = "Amount paid is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount paid must be greater than zero")
        BigDecimal amountPaid,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,

        @NotNull(message = "Payment date is required")
    @PastOrPresent(message = "Payment date cannot be in the future")
        LocalDate paymentDate
) {
}
