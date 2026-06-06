package rw.utility.utility_billing_system.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import rw.utility.utility_billing_system.enums.MeterType;
import rw.utility.utility_billing_system.enums.TariffType;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request body for creating or updating a tariff rate schedule.
 */

public record TariffRequest(
        @NotNull(message = "Meter type is required")
        MeterType meterType,

        @NotNull(message = "Tariff type is required")
        TariffType tariffType,

        @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than zero")
        BigDecimal unitPrice,

        @PositiveOrZero(message = "Minimum units cannot be negative")
        Double minUnits,

        @PositiveOrZero(message = "Maximum units cannot be negative")
        Double maxUnits,

        @NotNull(message = "Service charge is required")
    @PositiveOrZero(message = "Service charge cannot be negative")
        BigDecimal serviceCharge,

        @NotNull(message = "VAT rate is required")
    @PositiveOrZero(message = "VAT rate cannot be negative")
        // Percentage applied to unit and service charges
        Double vatRate,
        @NotNull(message = "Penalty rate is required")
    @PositiveOrZero(message = "Penalty rate cannot be negative")
        Double penaltyRate,

        @NotNull(message = "Effective date is required")
        LocalDate effectiveDate,

        boolean active
) {
}
