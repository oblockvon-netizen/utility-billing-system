package rw.utility.utility_billing_system.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.utility.utility_billing_system.dto.UserResponse;
import rw.utility.utility_billing_system.entity.User;
import rw.utility.utility_billing_system.enums.Status;
import rw.utility.utility_billing_system.exception.ResourceNotFoundException;
import rw.utility.utility_billing_system.repository.UserRepository;
import rw.utility.utility_billing_system.service.UserService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserMapper::toResponse);
    }

    @Override
    @Transactional
    public UserResponse updateStatus(UUID userId, Status status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setStatus(status);
        return UserMapper.toResponse(userRepository.save(user));
    }
}
