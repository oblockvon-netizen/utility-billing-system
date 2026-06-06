package rw.utility.utility_billing_system.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rw.utility.utility_billing_system.dto.MeterReadingRequest;
import rw.utility.utility_billing_system.dto.MeterReadingResponse;

import java.util.UUID;

/**
 * Business contract for capturing and querying monthly meter consumption readings.
 */

public interface MeterReadingService {

    // Enforces one reading per meter per calendar month
    MeterReadingResponse create(MeterReadingRequest request);
    MeterReadingResponse findById(UUID readingId);

    Page<MeterReadingResponse> findByMeter(UUID meterId, Pageable pageable);
}
