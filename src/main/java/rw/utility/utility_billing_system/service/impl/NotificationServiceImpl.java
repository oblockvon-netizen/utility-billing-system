package rw.utility.utility_billing_system.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.utility.utility_billing_system.dto.NotificationResponse;
import rw.utility.utility_billing_system.dto.UnreadCountResponse;
import rw.utility.utility_billing_system.entity.Customer;
import rw.utility.utility_billing_system.entity.Notification;
import rw.utility.utility_billing_system.entity.User;
import rw.utility.utility_billing_system.exception.BusinessRuleException;
import rw.utility.utility_billing_system.exception.ResourceNotFoundException;
import rw.utility.utility_billing_system.repository.CustomerRepository;
import rw.utility.utility_billing_system.repository.NotificationRepository;
import rw.utility.utility_billing_system.repository.UserRepository;
import rw.utility.utility_billing_system.service.NotificationService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
/**
 * Retrieves and updates notifications for admins and linked customer accounts.
 */
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> findAllSystemWide(Pageable pageable) {
        return notificationRepository.findAllByOrderBySentAtDesc(pageable)
                .map(NotificationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> findMyNotifications(Pageable pageable) {
        Customer customer = getLinkedCustomerForCurrentUser();
        if (customer == null) {
            return Page.empty(pageable);
        }
        return notificationRepository
                .findByCustomerIdOrderBySentAtDesc(customer.getId(), pageable)
                .map(NotificationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UnreadCountResponse getMyUnreadCount() {
        Customer customer = getLinkedCustomerForCurrentUser();
        if (customer == null) {
            return new UnreadCountResponse(0);
        }
        return new UnreadCountResponse(
                notificationRepository.countByCustomerIdAndReadFalse(customer.getId())
        );
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(UUID notificationId) {
        Customer customer = getLinkedCustomerForCurrentUser();
        if (customer == null) {
            throw new BusinessRuleException("No customer account is linked to your user profile");
        }

        Notification notification = notificationRepository
                .findByIdAndCustomerId(notificationId, customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        notification.setRead(true);
        return NotificationMapper.toResponse(notificationRepository.save(notification));
    }

    private Customer getLinkedCustomerForCurrentUser() {
        User user = getCurrentUser();
        return customerRepository.findByEmailIgnoreCase(user.getEmail()).orElse(null);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessRuleException("Authenticated user not found"));
    }
}
