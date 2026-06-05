package rw.utility.utility_billing_system.util;

import java.math.BigDecimal;

/**
 * Standard notification message templates for billing events.
 */
public final class NotificationMessages {

    private NotificationMessages() {
    }

    public static String billGenerated(
            String customerName,
            int month,
            int year,
            BigDecimal amount
    ) {
        return "Dear %s%nYour %d/%d utility bill of %s frw has been successfully processed"
                .formatted(customerName, month, year, formatAmount(amount));
    }

    public static String paymentConfirmed(String customerName, BigDecimal amount) {
        return "Dear %s%nYour payment of %s frw has been received. Your bill is now fully paid"
                .formatted(customerName, formatAmount(amount));
    }

    private static String formatAmount(BigDecimal amount) {
        return amount.stripTrailingZeros().toPlainString();
    }
}
