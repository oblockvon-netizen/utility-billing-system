package rw.utility.utility_billing_system.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rw.utility.utility_billing_system.dto.BillApprovalRequest;
import rw.utility.utility_billing_system.dto.BillResponse;

import java.util.UUID;

public interface BillService {

    BillResponse generateBill(UUID readingId);

    void generateMonthlyBills(int month, int year);

    BillResponse approve(UUID billId, BillApprovalRequest request);

    BillResponse findById(UUID billId);

    Page<BillResponse> findAll(Pageable pageable);
}
