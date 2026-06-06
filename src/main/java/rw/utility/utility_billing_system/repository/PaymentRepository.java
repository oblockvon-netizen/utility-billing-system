package rw.utility.utility_billing_system.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import rw.utility.utility_billing_system.entity.Payment;

import java.util.UUID;

/**
 * Spring Data repository for Payment records, optionally filtered by bill.
 */

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    // Returns payment history for a single bill
    Page<Payment> findByBillId(UUID billId, Pageable pageable);}
