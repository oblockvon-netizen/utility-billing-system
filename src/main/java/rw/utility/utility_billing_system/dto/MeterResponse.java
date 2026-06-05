package rw.utility.utility_billing_system.dto;

import rw.utility.utility_billing_system.enums.MeterType;
import rw.utility.utility_billing_system.enums.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record MeterResponse(
        UUID id,
        String meterNumber,
        MeterType meterType,
        LocalDate installationDate,
        Status status,
        UUID customerId,
        String customerName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
