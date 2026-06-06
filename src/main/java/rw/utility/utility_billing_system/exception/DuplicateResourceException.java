package rw.utility.utility_billing_system.exception;

/**
 * Unchecked exception when a unique resource constraint would be violated (HTTP 409).
 */

public class DuplicateResourceException extends RuntimeException {

    // Covers duplicate email, national ID, or bill per reading
    public DuplicateResourceException(String message) {
        super(message);
    }
}
