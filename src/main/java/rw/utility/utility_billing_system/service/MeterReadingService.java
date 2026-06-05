package rw.utility.utility_billing_system.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rw.utility.utility_billing_system.dto.MeterReadingRequest;
import rw.utility.utility_billing_system.dto.MeterReadingResponse;

import java.util.UUID;

public interface MeterReadingService {

    MeterReadingResponse create(MeterReadingRequest request);

    MeterReadingResponse findById(UUID readingId);

    Page<MeterReadingResponse> findByMeter(UUID meterId, Pageable pageable);
}
