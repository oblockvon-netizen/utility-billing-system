package rw.utility.utility_billing_system.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import rw.utility.utility_billing_system.entity.Notification;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data repository for customer notifications.
 */
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findAllByOrderBySentAtDesc(Pageable pageable);

    Page<Notification> findByCustomerIdOrderBySentAtDesc(UUID customerId, Pageable pageable);

    long countByCustomerIdAndReadFalse(UUID customerId);

    Optional<Notification> findByIdAndCustomerId(UUID id, UUID customerId);
}
