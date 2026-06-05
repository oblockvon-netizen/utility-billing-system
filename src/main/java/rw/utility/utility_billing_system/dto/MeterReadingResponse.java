package rw.utility.utility_billing_system.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record MeterReadingResponse(
        UUID id,
        UUID meterId,
        String meterNumber,
        Double previousReading,
        Double currentReading,
        Double consumption,
        LocalDate readingDate,
        int month,
        int year,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
