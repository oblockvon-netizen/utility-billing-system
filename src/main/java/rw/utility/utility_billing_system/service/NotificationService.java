package rw.utility.utility_billing_system.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rw.utility.utility_billing_system.dto.NotificationResponse;

import java.util.UUID;

public interface NotificationService {

    Page<NotificationResponse> getByUserId(UUID userId, Pageable pageable);
}
