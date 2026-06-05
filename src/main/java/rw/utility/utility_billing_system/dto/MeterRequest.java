package rw.utility.utility_billing_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import rw.utility.utility_billing_system.enums.MeterType;

import java.time.LocalDate;
import java.util.UUID;

public record MeterRequest(
        @NotBlank(message = "Meter number is required")
        String meterNumber,

        @NotNull(message = "Meter type is required")
        MeterType meterType,

        @NotNull(message = "Installation date is required")
        @PastOrPresent(message = "Installation date cannot be in the future")
        LocalDate installationDate,

        @NotNull(message = "Customer ID is required")
        UUID customerId
) {
}
