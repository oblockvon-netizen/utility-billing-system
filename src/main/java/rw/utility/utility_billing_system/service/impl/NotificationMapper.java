package rw.utility.utility_billing_system.service.impl;

import rw.utility.utility_billing_system.dto.NotificationResponse;
import rw.utility.utility_billing_system.entity.Notification;

/**
 * Maps Notification entities to NotificationResponse DTOs.
 */
final class NotificationMapper {

    private NotificationMapper() {
    }

    static NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getCustomer().getId(),
                notification.getCustomer().getFullName(),
                notification.getMessage(),
                notification.getType(),
                notification.isRead(),
                notification.getSentAt(),
                notification.getCreatedAt()
        );
    }
}
