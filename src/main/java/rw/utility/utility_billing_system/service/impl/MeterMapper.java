package rw.utility.utility_billing_system.service.impl;

import rw.utility.utility_billing_system.dto.MeterResponse;
import rw.utility.utility_billing_system.entity.Meter;

/**
 * Maps Meter entities to request/response DTOs.
 */

final class MeterMapper {

    private MeterMapper() {
    }

    // Includes customer id for cross-resource references
    static MeterResponse toResponse(Meter meter) {
        return new MeterResponse(
                meter.getId(),
                meter.getMeterNumber(),
                meter.getMeterType(),
                meter.getInstallationDate(),
                meter.getStatus(),
                meter.getCustomer().getId(),
                meter.getCustomer().getFullName(),
                meter.getCreatedAt(),
                meter.getUpdatedAt()
        );
    }
}
