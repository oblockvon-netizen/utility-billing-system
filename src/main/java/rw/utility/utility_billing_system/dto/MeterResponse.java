package rw.utility.utility_billing_system.dto;

import rw.utility.utility_billing_system.enums.MeterType;
import rw.utility.utility_billing_system.enums.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Meter details including type, serial number, and status.
 */

public record MeterResponse(
        UUID id,
        // Unique physical meter identifier
        String meterNumber,        MeterType meterType,
        LocalDate installationDate,
        Status status,
        UUID customerId,
        String customerName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
