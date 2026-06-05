package rw.utility.utility_billing_system.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rw.utility.utility_billing_system.dto.NotificationResponse;
import rw.utility.utility_billing_system.dto.UnreadCountResponse;

import java.util.UUID;

/**
 * Business contract for system-wide and customer-scoped notification access.
 */
public interface NotificationService {

    Page<NotificationResponse> findAllSystemWide(Pageable pageable);

    Page<NotificationResponse> findMyNotifications(Pageable pageable);

    UnreadCountResponse getMyUnreadCount();

    NotificationResponse markAsRead(UUID notificationId);
}
