package rw.utility.utility_billing_system.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import rw.utility.utility_billing_system.debug.AgentDebugLog;
import rw.utility.utility_billing_system.dto.ApiResponse;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
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
        // #region agent log
        AgentDebugLog.log(
                "GlobalExceptionHandler:handleInvalidSort",
                "H1",
                "invalid pageable sort property",
                Map.of(
                        "property", exception.getPropertyName(),
                        "type", exception.getType().getType().getSimpleName()
                ),
                null
        );
        // #endregion
        String message = "Invalid sort field '%s' for %s. Use query params like sort=email,asc"
                .formatted(exception.getPropertyName(), exception.getType().getType().getSimpleName());
        return error(HttpStatus.BAD_REQUEST, message);
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
