package rw.utility.utility_billing_system.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rw.utility.utility_billing_system.dto.PaymentRequest;
import rw.utility.utility_billing_system.dto.PaymentResponse;

import java.util.UUID;

public interface PaymentService {

    PaymentResponse recordPayment(PaymentRequest request);

    PaymentResponse findById(UUID paymentId);

    Page<PaymentResponse> findAll(UUID billId, Pageable pageable);
}
