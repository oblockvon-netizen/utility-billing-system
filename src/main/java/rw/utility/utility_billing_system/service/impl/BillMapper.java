package rw.utility.utility_billing_system.service.impl;

import rw.utility.utility_billing_system.dto.BillResponse;
import rw.utility.utility_billing_system.entity.Bill;

/**
 * Maps Bill entities to BillResponse DTOs for API output.
 */

final class BillMapper {

    // Static utility; not instantiated by the container
    private BillMapper() {
    }

    static BillResponse toResponse(Bill bill) {
        return new BillResponse(
                bill.getId(),
                bill.getCustomer().getId(),
                bill.getCustomer().getFullName(),
                bill.getMeter().getId(),
                bill.getMeter().getMeterNumber(),
                bill.getMeterReading().getId(),
                bill.getMonth(),
                bill.getYear(),
                bill.getTotalConsumption(),
                bill.getUnitCharge(),
                bill.getServiceCharge(),
                bill.getVatAmount(),
                bill.getPenaltyAmount(),
                bill.getTotalAmount(),
                bill.getAmountPaid(),
                bill.getOutstandingBalance(),
                bill.getStatus(),
                bill.getGeneratedDate(),
                bill.getDueDate(),
                bill.getApprovedBy(),
                bill.getCreatedAt(),
                bill.getUpdatedAt()
        );
    }
}
