package rw.utility.utility_billing_system.service.impl;

import rw.utility.utility_billing_system.dto.MeterResponse;
import rw.utility.utility_billing_system.entity.Meter;

final class MeterMapper {

    private MeterMapper() {
    }

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
