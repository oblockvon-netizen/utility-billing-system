package rw.utility.utility_billing_system.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

// Omits null data/errors fields from JSON output
@JsonInclude(JsonInclude.Include.NON_NULL)
/**
 * Standard API envelope wrapping success/error messages and optional payload data.
 */
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        List<String> errors
) {
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    public static ApiResponse<Void> error(String message, List<String> errors) {
        return new ApiResponse<>(false, message, null, errors);
    }
}
