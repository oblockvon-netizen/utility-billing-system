package rw.utility.utility_billing_system.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rw.utility.utility_billing_system.dto.UserResponse;
import rw.utility.utility_billing_system.enums.Status;

import java.util.Set;
import java.util.UUID;

/**
 * Business contract for administrative user listing, status, and role assignment.
 */

public interface UserService {

    Page<UserResponse> getAllUsers(Pageable pageable);

    UserResponse updateStatus(UUID userId, Status status);

    // Replaces the entire role set for the target user
    UserResponse setRoles(UUID userId, Set<rw.utility.utility_billing_system.enums.Role> roles);}
