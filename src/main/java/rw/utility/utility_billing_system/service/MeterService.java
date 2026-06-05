package rw.utility.utility_billing_system.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rw.utility.utility_billing_system.dto.MeterRequest;
import rw.utility.utility_billing_system.dto.MeterResponse;
import rw.utility.utility_billing_system.enums.Status;

import java.util.UUID;

public interface MeterService {

    MeterResponse create(MeterRequest request);

    MeterResponse update(UUID meterId, MeterRequest request);

    MeterResponse findById(UUID meterId);

    Page<MeterResponse> findByCustomer(UUID customerId, Pageable pageable);

    MeterResponse updateStatus(UUID meterId, Status status);
}
