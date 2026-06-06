package rw.utility.utility_billing_system.dto;

import rw.utility.utility_billing_system.enums.Status;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Customer profile returned by customer management endpoints.
 */

public record CustomerResponse(
        UUID id,
        String fullName,
        String nationalId,
        String email,
        String phoneNumber,
        String address,
        // Reflects whether the customer can receive new bills
        Status status,        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
