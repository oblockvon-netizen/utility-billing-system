package rw.utility.utility_billing_system.dto;

public record AuthResponse(
        String token,
        String tokenType,
        UserResponse user
) {
    public AuthResponse(String token, UserResponse user) {
        this(token, "Bearer", user);
    }
}
