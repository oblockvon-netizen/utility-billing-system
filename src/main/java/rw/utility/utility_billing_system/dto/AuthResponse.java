package rw.utility.utility_billing_system.dto;

/**
 * Login/signup response payload containing JWT token and user profile.
 */

public record AuthResponse(
        // Bearer token for subsequent authenticated requests
        String token,        String tokenType,
        UserResponse user
) {
    public AuthResponse(String token, UserResponse user) {
        this(token, "Bearer", user);
    }
}
