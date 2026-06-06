package rw.utility.utility_billing_system.exception;

/**
 * Unchecked exception when a meter already has a reading for the given month/year (HTTP 409).
 */

public class DuplicateReadingException extends RuntimeException {

    // Separate from DuplicateResourceException for clearer API messages
    public DuplicateReadingException(String message) {
        super(message);
    }
}
