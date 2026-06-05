package rw.utility.utility_billing_system.service.impl;

import rw.utility.utility_billing_system.dto.PenaltyResponse;
import rw.utility.utility_billing_system.entity.Penalty;

/**
 * Maps Penalty entities to PenaltyResponse DTOs.
 */
final class PenaltyMapper {

    private PenaltyMapper() {
    }

    static PenaltyResponse toResponse(Penalty penalty) {
        return new PenaltyResponse(
                penalty.getId(),
                penalty.getName(),
                penalty.getDescription(),
                penalty.getRate(),
                penalty.getDaysAfterDue(),
                penalty.isActive(),
                penalty.getCreatedAt(),
                penalty.getUpdatedAt()
        );
    }
}
