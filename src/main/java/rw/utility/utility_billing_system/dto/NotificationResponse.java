package rw.utility.utility_billing_system.dto;

import rw.utility.utility_billing_system.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        UUID customerId,
        String customerName,
        String message,
        NotificationType type,
        LocalDateTime sentAt,
        LocalDateTime createdAt
) {
}
