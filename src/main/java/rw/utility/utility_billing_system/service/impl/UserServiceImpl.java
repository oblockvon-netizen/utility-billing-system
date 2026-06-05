package rw.utility.utility_billing_system.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.utility.utility_billing_system.dto.UserResponse;
import rw.utility.utility_billing_system.entity.Role;
import rw.utility.utility_billing_system.entity.User;
import rw.utility.utility_billing_system.enums.Status;
import rw.utility.utility_billing_system.exception.ResourceNotFoundException;
import rw.utility.utility_billing_system.repository.RoleRepository;
import rw.utility.utility_billing_system.repository.UserRepository;
import rw.utility.utility_billing_system.service.UserService;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

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

    @Override
    @Transactional
    public UserResponse setRoles(UUID userId, Set<rw.utility.utility_billing_system.enums.Role> roles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Set<Role> roleEntities = roles.stream()
                .map(role -> roleRepository.findByName(role)
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + role)))
                .collect(Collectors.toCollection(HashSet::new));

        user.setRoles(roleEntities);
        return UserMapper.toResponse(userRepository.save(user));
    }
}
