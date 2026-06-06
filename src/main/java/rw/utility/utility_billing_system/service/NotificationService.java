package rw.utility.utility_billing_system.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rw.utility.utility_billing_system.dto.NotificationResponse;

import java.util.UUID;

/**
 * Business contract for retrieving user-scoped billing notifications.
 */

public interface NotificationService {

    // Customers may only query their own notifications
    Page<NotificationResponse> getByUserId(UUID userId, Pageable pageable);}
