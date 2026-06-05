package rw.utility.utility_billing_system.dto;

import rw.utility.utility_billing_system.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Notification item returned to clients for display in a user inbox.
 */
public record NotificationResponse(
        UUID id,
        UUID customerId,
        String customerName,
        String message,
        NotificationType type,
        boolean read,
        LocalDateTime sentAt,
        LocalDateTime createdAt
) {
}
