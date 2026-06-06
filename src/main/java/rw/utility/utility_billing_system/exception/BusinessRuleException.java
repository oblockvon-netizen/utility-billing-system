package rw.utility.utility_billing_system.exception;

/**
 * Unchecked exception for domain rule violations (HTTP 422).
 */

public class BusinessRuleException extends RuntimeException {

    // Thrown when input is valid but breaks a billing business rule
    public BusinessRuleException(String message) {
        super(message);
    }
}
