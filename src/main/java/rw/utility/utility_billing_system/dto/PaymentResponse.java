package rw.utility.utility_billing_system.dto;

import rw.utility.utility_billing_system.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID billId,
        BigDecimal amountPaid,
        PaymentMethod paymentMethod,
        LocalDate paymentDate,
        String reference,
        BigDecimal billOutstandingBalance,
        LocalDateTime createdAt
) {
}
