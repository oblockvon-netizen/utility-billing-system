package rw.utility.utility_billing_system.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Captured meter reading with computed consumption.
 */

public record MeterReadingResponse(
        UUID id,
        UUID meterId,
        String meterNumber,
        Double previousReading,
        Double currentReading,
        // Derived as currentReading minus previousReading
        Double consumption,        LocalDate readingDate,
        int month,
        int year,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
