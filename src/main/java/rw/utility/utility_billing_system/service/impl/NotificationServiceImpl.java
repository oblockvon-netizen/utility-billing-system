package rw.utility.utility_billing_system.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.utility.utility_billing_system.dto.NotificationResponse;
import rw.utility.utility_billing_system.entity.Customer;
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
 * Retrieves paginated notifications for a given user.
 */
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getByUserId(UUID userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        assertCanAccessUserNotifications(user);

        Customer customer = customerRepository.findByEmailIgnoreCase(user.getEmail())
                .orElse(null);

        if (customer == null) {
            // Users without a linked customer record have no notifications
            return Page.empty(pageable);        }

        return notificationRepository
                .findByCustomerIdOrderBySentAtDesc(customer.getId(), pageable)
                .map(NotificationMapper::toResponse);
    }

    private void assertCanAccessUserNotifications(User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessRuleException("Authentication is required");
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);

        if (isAdmin) {
            return;
        }

        User authenticatedUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BusinessRuleException("Authenticated user not found"));

        if (!authenticatedUser.getId().equals(user.getId())) {
            throw new BusinessRuleException("You can only view your own notifications");
        }
    }
}
