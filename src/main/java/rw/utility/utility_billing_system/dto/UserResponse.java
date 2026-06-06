package rw.utility.utility_billing_system.dto;

import rw.utility.utility_billing_system.enums.Role;
import rw.utility.utility_billing_system.enums.Status;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Safe user profile for admin listing (no password or sensitive fields).
 */

public record UserResponse(
        UUID id,
        String fullName,
        String email,
        String phoneNumber,
        Status status,
        // Current authorities granted to the user
        Set<Role> roles,        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
