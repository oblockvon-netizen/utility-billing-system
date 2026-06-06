package rw.utility.utility_billing_system.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request body for operators to submit a monthly meter reading.
 */

public record MeterReadingRequest(
        @NotNull(message = "Meter ID is required")
        UUID meterId,

        @NotNull(message = "Previous reading is required")
    @PositiveOrZero(message = "Previous reading cannot be negative")
        Double previousReading,

        @NotNull(message = "Current reading is required")
    @PositiveOrZero(message = "Current reading cannot be negative")
        // Must exceed previousReading for valid consumption
        Double currentReading,
        @NotNull(message = "Reading date is required")
    @PastOrPresent(message = "Reading date cannot be in the future")
        LocalDate readingDate,

        @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
        int month,

        @Min(value = 2000, message = "Year must be 2000 or later")
        int year
) {
}
