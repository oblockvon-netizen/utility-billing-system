package rw.utility.utility_billing_system.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.utility.utility_billing_system.dto.PaymentRequest;
import rw.utility.utility_billing_system.dto.PaymentResponse;
import rw.utility.utility_billing_system.entity.Bill;
import rw.utility.utility_billing_system.entity.Payment;
import rw.utility.utility_billing_system.enums.BillStatus;
import rw.utility.utility_billing_system.exception.BusinessRuleException;
import rw.utility.utility_billing_system.exception.ResourceNotFoundException;
import rw.utility.utility_billing_system.repository.BillRepository;
import rw.utility.utility_billing_system.repository.PaymentRepository;
import rw.utility.utility_billing_system.service.PaymentService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
/**
 * Records payments, updates bill balances, and marks bills PAID when fully settled.
 */
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BillRepository billRepository;

    @Override
    @Transactional
    public PaymentResponse recordPayment(PaymentRequest request) {
        Bill bill = billRepository.findById(request.billId())
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found"));
        if (bill.getStatus() == BillStatus.PAID) {
            throw new BusinessRuleException("This bill is already paid");
        }

        BigDecimal amount = request.amountPaid().setScale(2, RoundingMode.HALF_UP);
        Payment payment = Payment.builder()
                .bill(bill)
                .amountPaid(amount)
                .paymentMethod(request.paymentMethod())
                .paymentDate(request.paymentDate())
                .reference(UUID.randomUUID().toString())
                .build();
        Payment savedPayment = paymentRepository.save(payment);

        bill.setAmountPaid(bill.getAmountPaid().add(amount).setScale(2, RoundingMode.HALF_UP));
        BigDecimal outstandingBalance = bill.getOutstandingBalance().subtract(amount)
                .setScale(2, RoundingMode.HALF_UP);
        // Partial payments keep the bill open until balance reaches zero
        if (outstandingBalance.compareTo(BigDecimal.ZERO) <= 0) {            outstandingBalance = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            bill.setStatus(BillStatus.PAID);
        }
        bill.setOutstandingBalance(outstandingBalance);
        billRepository.save(bill);

        return PaymentMapper.toResponse(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse findById(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        return PaymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponse> findAll(UUID billId, Pageable pageable) {
        if (billId == null) {
        return paymentRepository.findAll(pageable).map(PaymentMapper::toResponse);
        }
        if (!billRepository.existsById(billId)) {
            throw new ResourceNotFoundException("Bill not found");
        }
        return paymentRepository.findByBillId(billId, pageable).map(PaymentMapper::toResponse);
    }
}
