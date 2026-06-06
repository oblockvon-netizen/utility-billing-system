package rw.utility.utility_billing_system.exception;

/**
 * Unchecked exception when meter reading values fail validation (HTTP 400).
 */

public class InvalidReadingException extends RuntimeException {

    // e.g. current reading less than or equal to previous reading
    public InvalidReadingException(String message) {
        super(message);
    }
}
