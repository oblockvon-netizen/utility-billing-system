package rw.utility.utility_billing_system.util;

/**
 * Shared validation patterns for Rwandan customer and user data.
 */
public final class ValidationPatterns {

    public static final String NATIONAL_ID = "^.{18}$";

    public static final String PHONE_NUMBER = "^(078|079)\\d{7}$";

    public static final String NATIONAL_ID_MESSAGE =
            "National ID must be exactly 18 characters";

    public static final String PHONE_NUMBER_MESSAGE =
            "Phone number must be 10 digits starting with 078 or 079";

    private ValidationPatterns() {
    }
}
