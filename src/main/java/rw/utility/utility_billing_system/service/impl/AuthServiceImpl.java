package rw.utility.utility_billing_system.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.utility.utility_billing_system.dto.AuthResponse;
import rw.utility.utility_billing_system.dto.LoginRequest;
import rw.utility.utility_billing_system.dto.SignupRequest;
import rw.utility.utility_billing_system.entity.Role;
import rw.utility.utility_billing_system.entity.User;
import rw.utility.utility_billing_system.enums.Status;
import rw.utility.utility_billing_system.exception.BusinessRuleException;
import rw.utility.utility_billing_system.exception.DuplicateResourceException;
import rw.utility.utility_billing_system.exception.ResourceNotFoundException;
import rw.utility.utility_billing_system.repository.RoleRepository;
import rw.utility.utility_billing_system.repository.UserRepository;
import rw.utility.utility_billing_system.security.JwtUtil;
import rw.utility.utility_billing_system.security.UserDetailsServiceImpl;
import rw.utility.utility_billing_system.service.AuthService;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
/**
 * Handles user registration with password hashing and JWT issuance on login.
 */
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public AuthResponse signup(SignupRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("A user with this email already exists");
        }

        rw.utility.utility_billing_system.enums.Role signupRole = request.role() != null
                ? request.role()
                : rw.utility.utility_billing_system.enums.Role.ROLE_CUSTOMER;
        if (signupRole != rw.utility.utility_billing_system.enums.Role.ROLE_CUSTOMER) {
            // Non-customer roles must be assigned by an administrator
            throw new BusinessRuleException("Public signup only permits ROLE_CUSTOMER");        }

        Role customerRole = roleRepository
                .findByName(rw.utility.utility_billing_system.enums.Role.ROLE_CUSTOMER)
                .orElseThrow(() -> new ResourceNotFoundException("ROLE_CUSTOMER is not configured"));

        User user = User.builder()
                .fullName(request.fullName().trim())
                .email(email)
                .phoneNumber(request.phoneNumber().trim())
                .password(passwordEncoder.encode(request.password()))
                .status(Status.ACTIVE)
                .roles(new HashSet<>(Set.of(customerRole)))
                .build();

        User savedUser = userRepository.save(user);
        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
        return new AuthResponse(jwtUtil.generateToken(userDetails), UserMapper.toResponse(savedUser));
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password())
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new AuthResponse(jwtUtil.generateToken(userDetails), UserMapper.toResponse(user));
    }
}
