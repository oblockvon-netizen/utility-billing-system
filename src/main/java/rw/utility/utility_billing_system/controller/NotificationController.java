package rw.utility.utility_billing_system.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rw.utility.utility_billing_system.dto.ApiResponse;
import rw.utility.utility_billing_system.dto.NotificationResponse;
import rw.utility.utility_billing_system.dto.UnreadCountResponse;
import rw.utility.utility_billing_system.service.NotificationService;

import java.util.UUID;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Notifications", description = "System-wide and customer notification access")
/**
 * REST resource for admin system-wide notifications and customer inbox actions.
 */
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> findAllSystemWide(
            Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Notifications retrieved successfully",
                notificationService.findAllSystemWide(pageable)
        ));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> findMyNotifications(
            Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Your notifications retrieved successfully",
                notificationService.findMyNotifications(pageable)
        ));
    }

    @GetMapping("/my/unread-count")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse<UnreadCountResponse>> getMyUnreadCount() {
        return ResponseEntity.ok(ApiResponse.success(
                "Unread notification count retrieved successfully",
                notificationService.getMyUnreadCount()
        ));
    }

    @PatchMapping("/{id}/read")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Notification marked as read",
                notificationService.markAsRead(id)
        ));
    }
}
