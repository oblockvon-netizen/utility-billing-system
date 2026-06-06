package rw.utility.utility_billing_system.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rw.utility.utility_billing_system.dto.ApiResponse;
import rw.utility.utility_billing_system.dto.NotificationResponse;
import rw.utility.utility_billing_system.service.NotificationService;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CUSTOMER')")
@Tag(name = "Notifications", description = "User notification retrieval")
/**
 * REST resource for paginated user notifications. Accessible to ADMIN and CUSTOMER roles.
 */
public class NotificationController {

    private final NotificationService notificationService;

    // Returns newest notifications first via default page sort
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getByUserId(
            @PathVariable UUID userId,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Notifications retrieved successfully",
                notificationService.getByUserId(userId, pageable)
        ));
    }
}
