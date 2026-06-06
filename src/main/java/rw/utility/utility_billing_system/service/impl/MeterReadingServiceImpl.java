package rw.utility.utility_billing_system.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.utility.utility_billing_system.dto.MeterReadingRequest;
import rw.utility.utility_billing_system.dto.MeterReadingResponse;
import rw.utility.utility_billing_system.entity.Meter;
import rw.utility.utility_billing_system.entity.MeterReading;
import rw.utility.utility_billing_system.enums.Status;
import rw.utility.utility_billing_system.exception.BusinessRuleException;
import rw.utility.utility_billing_system.exception.DuplicateReadingException;
import rw.utility.utility_billing_system.exception.InvalidReadingException;
import rw.utility.utility_billing_system.exception.ResourceNotFoundException;
import rw.utility.utility_billing_system.repository.MeterReadingRepository;
import rw.utility.utility_billing_system.repository.MeterRepository;
import rw.utility.utility_billing_system.service.MeterReadingService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
/**
 * Captures meter readings with consumption validation and duplicate-month guards.
 */
public class MeterReadingServiceImpl implements MeterReadingService {

    private final MeterReadingRepository meterReadingRepository;
    private final MeterRepository meterRepository;

    @Override
    @Transactional
    public MeterReadingResponse create(MeterReadingRequest request) {
        Meter meter = meterRepository.findById(request.meterId())
                .orElseThrow(() -> new ResourceNotFoundException("Meter not found"));

        if (meter.getStatus() == Status.INACTIVE) {
            throw new BusinessRuleException("Readings cannot be captured for inactive meters");
        }

        // Meters only advance; rollback requires a correction workflow
        if (request.currentReading() <= request.previousReading()) {            throw new InvalidReadingException(
                    "Current reading must be greater than previous reading"
            );
        }

        if (meterReadingRepository.existsByMeterIdAndMonthAndYear(
                request.meterId(),
                request.month(),
                request.year()
        )) {
            throw new DuplicateReadingException(
                    "A reading already exists for this meter, month, and year"
            );
        }

        MeterReading reading = MeterReading.builder()
                .meter(meter)
                .previousReading(request.previousReading())
                .currentReading(request.currentReading())
                .consumption(request.currentReading() - request.previousReading())
                .readingDate(request.readingDate())
                .month(request.month())
                .year(request.year())
                .build();
        return MeterReadingMapper.toResponse(meterReadingRepository.save(reading));
    }

    @Override
    @Transactional(readOnly = true)
    public MeterReadingResponse findById(UUID readingId) {
        MeterReading reading = meterReadingRepository.findById(readingId)
                .orElseThrow(() -> new ResourceNotFoundException("Meter reading not found"));
        return MeterReadingMapper.toResponse(reading);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MeterReadingResponse> findByMeter(UUID meterId, Pageable pageable) {
        if (!meterRepository.existsById(meterId)) {
            throw new ResourceNotFoundException("Meter not found");
        }
        return meterReadingRepository.findByMeterId(meterId, pageable)
                .map(MeterReadingMapper::toResponse);
    }
}
