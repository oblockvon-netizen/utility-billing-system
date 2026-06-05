package rw.utility.utility_billing_system.service;

import rw.utility.utility_billing_system.dto.AuthResponse;
import rw.utility.utility_billing_system.dto.LoginRequest;
import rw.utility.utility_billing_system.dto.SignupRequest;

public interface AuthService {

    AuthResponse signup(SignupRequest request);

    AuthResponse login(LoginRequest request);
}
