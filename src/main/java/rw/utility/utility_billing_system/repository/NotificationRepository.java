package rw.utility.utility_billing_system.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import rw.utility.utility_billing_system.entity.Notification;

import java.util.UUID;

/**
 * Spring Data repository for paginated notifications by user.
 */

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    // Newest notifications appear first
    Page<Notification> findByCustomerIdOrderBySentAtDesc(UUID customerId, Pageable pageable);}
