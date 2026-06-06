package rw.utility.utility_billing_system.enums;

/**
 * Lifecycle states for a utility bill from generation through payment.
 */

public enum BillStatus {
    // Awaiting finance/admin approval before customer payment
    PENDING,    APPROVED,
    PAID,
    OVERDUE
}
