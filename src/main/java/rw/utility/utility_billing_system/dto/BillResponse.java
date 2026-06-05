package rw.utility.utility_billing_system.dto;

import rw.utility.utility_billing_system.enums.BillStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record BillResponse(
        UUID id,
        UUID customerId,
        String customerName,
        UUID meterId,
        String meterNumber,
        UUID meterReadingId,
        int month,
        int year,
        Double totalConsumption,
        BigDecimal unitCharge,
        BigDecimal serviceCharge,
        BigDecimal vatAmount,
        BigDecimal penaltyAmount,
        BigDecimal totalAmount,
        BigDecimal amountPaid,
        BigDecimal outstandingBalance,
        BillStatus status,
        LocalDate generatedDate,
        LocalDate dueDate,
        String approvedBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
