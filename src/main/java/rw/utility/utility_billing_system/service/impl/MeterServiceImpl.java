package rw.utility.utility_billing_system.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.utility.utility_billing_system.dto.MeterRequest;
import rw.utility.utility_billing_system.dto.MeterResponse;
import rw.utility.utility_billing_system.entity.Customer;
import rw.utility.utility_billing_system.entity.Meter;
import rw.utility.utility_billing_system.enums.Status;
import rw.utility.utility_billing_system.exception.BusinessRuleException;
import rw.utility.utility_billing_system.exception.DuplicateResourceException;
import rw.utility.utility_billing_system.exception.ResourceNotFoundException;
import rw.utility.utility_billing_system.repository.CustomerRepository;
import rw.utility.utility_billing_system.repository.MeterRepository;
import rw.utility.utility_billing_system.service.MeterService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
/**
 * Registers meters for customers and enforces active-customer constraints.
 */
public class MeterServiceImpl implements MeterService {

    private final MeterRepository meterRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public MeterResponse create(MeterRequest request) {
        String meterNumber = normalize(request.meterNumber());
        ensureMeterNumberAvailable(meterNumber);
        Customer customer = getCustomer(request.customerId());
        ensureCustomerActive(customer);

        Meter meter = Meter.builder()
                .meterNumber(meterNumber)
                .meterType(request.meterType())
                .installationDate(request.installationDate())
                .status(Status.ACTIVE)
                .customer(customer)
                .build();
        return MeterMapper.toResponse(meterRepository.save(meter));
    }

    @Override
    @Transactional
    public MeterResponse update(UUID meterId, MeterRequest request) {
        Meter meter = getMeter(meterId);
        String meterNumber = normalize(request.meterNumber());
        if (!meter.getMeterNumber().equals(meterNumber)) {
            ensureMeterNumberAvailable(meterNumber);
        }

        Customer customer = getCustomer(request.customerId());
        if (!meter.getCustomer().getId().equals(customer.getId())) {
            ensureCustomerActive(customer);
        }

        meter.setMeterNumber(meterNumber);
        meter.setMeterType(request.meterType());
        meter.setInstallationDate(request.installationDate());
        meter.setCustomer(customer);
        return MeterMapper.toResponse(meterRepository.save(meter));
    }

    @Override
    @Transactional(readOnly = true)
    public MeterResponse findById(UUID meterId) {
        return MeterMapper.toResponse(getMeter(meterId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MeterResponse> findByCustomer(UUID customerId, Pageable pageable) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found");
        }
        return meterRepository.findByCustomerId(customerId, pageable).map(MeterMapper::toResponse);
    }

    @Override
    @Transactional
    public MeterResponse updateStatus(UUID meterId, Status status) {
        Meter meter = getMeter(meterId);
        meter.setStatus(status);
        return MeterMapper.toResponse(meterRepository.save(meter));
    }

    private Meter getMeter(UUID meterId) {
        return meterRepository.findById(meterId)
                .orElseThrow(() -> new ResourceNotFoundException("Meter not found"));
    }

    private Customer getCustomer(UUID customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    private void ensureMeterNumberAvailable(String meterNumber) {
        if (meterRepository.existsByMeterNumber(meterNumber)) {
            throw new DuplicateResourceException("A meter with this meter number already exists");
        }
    }

    private void ensureCustomerActive(Customer customer) {
        if (customer.getStatus() != Status.ACTIVE) {
            // Prevents billing setup for suspended accounts
            throw new BusinessRuleException("Inactive customers cannot be assigned new meters");        }
    }

    private String normalize(String value) {
        return value.trim().toUpperCase();
    }
}
