package rw.utility.utility_billing_system.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rw.utility.utility_billing_system.dto.ApiResponse;
import rw.utility.utility_billing_system.dto.SetUserRolesRequest;
import rw.utility.utility_billing_system.dto.UpdateUserStatusRequest;
import rw.utility.utility_billing_system.dto.UserResponse;
import rw.utility.utility_billing_system.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Users", description = "Administrative user management")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.success("Users retrieved successfully", userService.getAllUsers(pageable))
        );
    }

    @PutMapping("/{userId}/status")
    public ResponseEntity<ApiResponse<UserResponse>> updateStatus(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserStatusRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "User status updated successfully",
                        userService.updateStatus(userId, request.status())
                )
        );
    }

    @PutMapping("/{userId}/roles")
    public ResponseEntity<ApiResponse<UserResponse>> setRoles(
            @PathVariable UUID userId,
            @Valid @RequestBody SetUserRolesRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "User roles updated successfully",
                        userService.setRoles(userId, request.roles())
                )
        );
    }
}
