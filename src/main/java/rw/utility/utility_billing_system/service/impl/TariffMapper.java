package rw.utility.utility_billing_system.service.impl;

import rw.utility.utility_billing_system.dto.TariffResponse;
import rw.utility.utility_billing_system.entity.Tariff;

/**
 * Maps Tariff entities to request/response DTOs.
 */

final class TariffMapper {

    private TariffMapper() {
    }

    // Surfaces effective date used during bill calculation
    static TariffResponse toResponse(Tariff tariff) {
        return new TariffResponse(
                tariff.getId(),
                tariff.getMeterType(),
                tariff.getTariffType(),
                tariff.getUnitPrice(),
                tariff.getMinUnits(),
                tariff.getMaxUnits(),
                tariff.getServiceCharge(),
                tariff.getVatRate(),
                tariff.getPenaltyRate(),
                tariff.getEffectiveDate(),
                tariff.isActive(),
                tariff.getCreatedAt(),
                tariff.getUpdatedAt()
        );
    }
}
