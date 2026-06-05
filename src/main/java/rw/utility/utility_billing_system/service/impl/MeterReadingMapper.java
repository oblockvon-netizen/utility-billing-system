package rw.utility.utility_billing_system.service.impl;

import rw.utility.utility_billing_system.dto.MeterReadingResponse;
import rw.utility.utility_billing_system.entity.MeterReading;

final class MeterReadingMapper {

    private MeterReadingMapper() {
    }

    static MeterReadingResponse toResponse(MeterReading reading) {
        return new MeterReadingResponse(
                reading.getId(),
                reading.getMeter().getId(),
                reading.getMeter().getMeterNumber(),
                reading.getPreviousReading(),
                reading.getCurrentReading(),
                reading.getConsumption(),
                reading.getReadingDate(),
                reading.getMonth(),
                reading.getYear(),
                reading.getCreatedAt(),
                reading.getUpdatedAt()
        );
    }
}
