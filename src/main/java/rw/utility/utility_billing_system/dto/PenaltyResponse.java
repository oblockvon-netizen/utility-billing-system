package rw.utility.utility_billing_system.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Penalty rule returned to admin clients.
 */
public record PenaltyResponse(
        UUID id,
        String name,
        String description,
        Double rate,
        Integer daysAfterDue,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
