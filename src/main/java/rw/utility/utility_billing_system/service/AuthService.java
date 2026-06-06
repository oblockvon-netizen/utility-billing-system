package rw.utility.utility_billing_system.service;

import rw.utility.utility_billing_system.dto.AuthResponse;
import rw.utility.utility_billing_system.dto.LoginRequest;
import rw.utility.utility_billing_system.dto.SignupRequest;

/**
 * Authentication contract for public signup and credential-based login.
 */

public interface AuthService {

    // Signup always issues a JWT so the client can authenticate immediately
    AuthResponse signup(SignupRequest request);
    AuthResponse login(LoginRequest request);
}
