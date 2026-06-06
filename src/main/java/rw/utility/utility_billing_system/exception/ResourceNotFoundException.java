package rw.utility.utility_billing_system.exception;

/**
 * Unchecked exception when a requested entity does not exist (HTTP 404).
 */

public class ResourceNotFoundException extends RuntimeException {

    // Message identifies which entity was not found
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
