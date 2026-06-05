package rw.utility.utility_billing_system.service.impl;

import rw.utility.utility_billing_system.dto.PaymentResponse;
import rw.utility.utility_billing_system.entity.Payment;

final class PaymentMapper {

    private PaymentMapper() {
    }

    static PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getBill().getId(),
                payment.getAmountPaid(),
                payment.getPaymentMethod(),
                payment.getPaymentDate(),
                payment.getReference(),
                payment.getBill().getOutstandingBalance(),
                payment.getCreatedAt()
        );
    }
}
