package rw.utility.utility_billing_system.service.impl;

import rw.utility.utility_billing_system.dto.CustomerResponse;
import rw.utility.utility_billing_system.entity.Customer;

final class CustomerMapper {

    private CustomerMapper() {
    }

    static CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getFullName(),
                customer.getNationalId(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getAddress(),
                customer.getStatus(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }
}
