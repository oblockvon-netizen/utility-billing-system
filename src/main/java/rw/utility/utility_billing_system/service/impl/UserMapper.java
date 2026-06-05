package rw.utility.utility_billing_system.service.impl;

import rw.utility.utility_billing_system.dto.UserResponse;
import rw.utility.utility_billing_system.entity.User;

import java.util.stream.Collectors;

final class UserMapper {

    private UserMapper() {
    }

    static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getStatus(),
                user.getRoles().stream()
                        .map(rw.utility.utility_billing_system.entity.Role::getName)
                        .collect(Collectors.toUnmodifiableSet()),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
