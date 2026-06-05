package rw.utility.utility_billing_system.dto;

import rw.utility.utility_billing_system.enums.MeterType;
import rw.utility.utility_billing_system.enums.TariffType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TariffResponse(
        UUID id,
        MeterType meterType,
        TariffType tariffType,
        BigDecimal unitPrice,
        Double minUnits,
        Double maxUnits,
        BigDecimal serviceCharge,
        Double vatRate,
        Double penaltyRate,
        LocalDate effectiveDate,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
