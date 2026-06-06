package rw.utility.utility_billing_system.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rw.utility.utility_billing_system.dto.CustomerRequest;
import rw.utility.utility_billing_system.dto.CustomerResponse;
import rw.utility.utility_billing_system.enums.Status;

import java.util.UUID;

/**
 * Business contract for customer registration, updates, and status management.
 */

public interface CustomerService {

    CustomerResponse create(CustomerRequest request);

    CustomerResponse update(UUID customerId, CustomerRequest request);

    CustomerResponse findById(UUID customerId);

    Page<CustomerResponse> findAll(Pageable pageable);

    // Inactive customers are blocked from receiving new bills
    CustomerResponse updateStatus(UUID customerId, Status status);}
