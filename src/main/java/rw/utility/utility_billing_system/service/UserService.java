package rw.utility.utility_billing_system.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rw.utility.utility_billing_system.dto.UserResponse;
import rw.utility.utility_billing_system.enums.Status;

import java.util.UUID;

public interface UserService {

    Page<UserResponse> getAllUsers(Pageable pageable);

    UserResponse updateStatus(UUID userId, Status status);
}
