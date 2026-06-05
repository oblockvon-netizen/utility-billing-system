package rw.utility.utility_billing_system.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.utility.utility_billing_system.dto.CustomerRequest;
import rw.utility.utility_billing_system.dto.CustomerResponse;
import rw.utility.utility_billing_system.entity.Customer;
import rw.utility.utility_billing_system.enums.Status;
import rw.utility.utility_billing_system.exception.BusinessRuleException;
import rw.utility.utility_billing_system.exception.DuplicateResourceException;
import rw.utility.utility_billing_system.exception.ResourceNotFoundException;
import rw.utility.utility_billing_system.util.ValidationPatterns;
import rw.utility.utility_billing_system.repository.CustomerRepository;
import rw.utility.utility_billing_system.service.CustomerService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
/**
 * Persists and manages utility customer records linked to user accounts.
 */
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        String nationalId = normalizeNationalId(request.nationalId());
        String phoneNumber = normalizePhoneNumber(request.phoneNumber());
        ensureNationalIdAvailable(nationalId);

        Customer customer = Customer.builder()
                .fullName(request.fullName().trim())
                .nationalId(nationalId)
                .email(request.email().trim().toLowerCase())
                .phoneNumber(phoneNumber)
                .address(request.address().trim())
                .status(Status.ACTIVE)
                .build();
        return CustomerMapper.toResponse(customerRepository.save(customer));
    }

    @Override
    @Transactional
    public CustomerResponse update(UUID customerId, CustomerRequest request) {
        Customer customer = getCustomer(customerId);
        String nationalId = normalizeNationalId(request.nationalId());
        String phoneNumber = normalizePhoneNumber(request.phoneNumber());
        if (!customer.getNationalId().equals(nationalId)) {
            ensureNationalIdAvailable(nationalId);
        }

        customer.setFullName(request.fullName().trim());
        customer.setNationalId(nationalId);
        customer.setEmail(request.email().trim().toLowerCase());
        customer.setPhoneNumber(phoneNumber);
        customer.setAddress(request.address().trim());
        return CustomerMapper.toResponse(customerRepository.save(customer));
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse findById(UUID customerId) {
        return CustomerMapper.toResponse(getCustomer(customerId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponse> findAll(Pageable pageable) {
        return customerRepository.findAll(pageable).map(CustomerMapper::toResponse);
    }

    @Override
    @Transactional
    public CustomerResponse updateStatus(UUID customerId, Status status) {
        Customer customer = getCustomer(customerId);
        customer.setStatus(status);
        return CustomerMapper.toResponse(customerRepository.save(customer));
    }

    private Customer getCustomer(UUID customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    private void ensureNationalIdAvailable(String nationalId) {
        if (customerRepository.existsByNationalId(nationalId)) {
            throw new DuplicateResourceException("A customer with this national ID already exists");
        }
    }

    private String normalizeNationalId(String value) {
        String nationalId = value.trim().toUpperCase();
        if (nationalId.length() != 18) {
            throw new BusinessRuleException(ValidationPatterns.NATIONAL_ID_MESSAGE);
        }
        return nationalId;
    }

    private String normalizePhoneNumber(String value) {
        String phoneNumber = value.trim();
        if (!phoneNumber.matches(ValidationPatterns.PHONE_NUMBER)) {
            throw new BusinessRuleException(ValidationPatterns.PHONE_NUMBER_MESSAGE);
        }
        return phoneNumber;
    }
}
