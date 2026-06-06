package rw.utility.utility_billing_system.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import rw.utility.utility_billing_system.dto.ApiResponse;

import java.util.List;

@RestControllerAdvice
/**
 * Maps domain and framework exceptions to consistent ApiResponse HTTP error payloads.
 */
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler({DuplicateResourceException.class, DataIntegrityViolationException.class})
    public ResponseEntity<ApiResponse<Void>> handleDuplicate(RuntimeException exception) {
        return error(HttpStatus.CONFLICT, "Resource already exists");
    }

    @ExceptionHandler(DuplicateReadingException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateReading(
            DuplicateReadingException exception
    ) {
        return error(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(InvalidReadingException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidReading(
            InvalidReadingException exception
    ) {
        return error(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessRule(BusinessRuleException exception) {
        return error(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthentication(AuthenticationException exception) {
        return error(HttpStatus.UNAUTHORIZED, "Invalid email, password, or account status");
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidSort(PropertyReferenceException exception) {
        String message = "Invalid sort field '%s' for %s. Use query params like sort=email,asc"
                .formatted(exception.getPropertyName(), exception.getType().getType().getSimpleName());
        return error(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMalformedJson(
            HttpMessageNotReadableException exception
    ) {
        if (exception.getCause() instanceof InvalidFormatException invalidFormat) {
            String field = invalidFormat.getPath().isEmpty()
                    ? "request body"
                    : invalidFormat.getPath().get(0).getFieldName();
            String message = "Invalid value for '%s'. Expected %s"
                    .formatted(field, invalidFormat.getTargetType().getSimpleName());
            return error(HttpStatus.BAD_REQUEST, message);
        }
        return error(HttpStatus.BAD_REQUEST, "Invalid request body format");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception
    ) {
        String expectedType = exception.getRequiredType() != null
                ? exception.getRequiredType().getSimpleName()
                : "valid type";
        String message = "Invalid value for '%s'. Expected %s"
                .formatted(exception.getName(), expectedType);
        return error(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataAccess(DataAccessException exception) {
        String causeMessage = exception.getMostSpecificCause().getMessage();
        // Surface an actionable hint when the DB routine was not installed
        if (causeMessage != null && causeMessage.contains("generate_monthly_bills")) {
        return error(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Database routine generate_monthly_bills is missing. Ensure routines.sql ran on startup."
            );
        }
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "A database error occurred");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(
            MethodArgumentNotValidException exception
    ) {
        List<String> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("Validation failed", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception exception) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    private ResponseEntity<ApiResponse<Void>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(ApiResponse.error(message, List.of(message)));
    }
}
