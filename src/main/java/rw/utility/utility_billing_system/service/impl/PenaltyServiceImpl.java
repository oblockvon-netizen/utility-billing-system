package rw.utility.utility_billing_system.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.utility.utility_billing_system.dto.PenaltyRequest;
import rw.utility.utility_billing_system.dto.PenaltyResponse;
import rw.utility.utility_billing_system.entity.Penalty;
import rw.utility.utility_billing_system.exception.ResourceNotFoundException;
import rw.utility.utility_billing_system.repository.PenaltyRepository;
import rw.utility.utility_billing_system.service.PenaltyService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
/**
 * Creates and manages configurable late-payment penalty rules.
 */
public class PenaltyServiceImpl implements PenaltyService {

    private final PenaltyRepository penaltyRepository;

    @Override
    @Transactional
    public PenaltyResponse create(PenaltyRequest request) {
        Penalty penalty = Penalty.builder()
                .name(request.name().trim())
                .description(request.description() != null ? request.description().trim() : null)
                .rate(request.rate())
                .daysAfterDue(request.daysAfterDue())
                .active(true)
                .build();
        return PenaltyMapper.toResponse(penaltyRepository.save(penalty));
    }

    @Override
    @Transactional
    public PenaltyResponse toggleActive(UUID penaltyId) {
        Penalty penalty = penaltyRepository.findById(penaltyId)
                .orElseThrow(() -> new ResourceNotFoundException("Penalty rule not found"));
        penalty.setActive(!penalty.isActive());
        return PenaltyMapper.toResponse(penaltyRepository.save(penalty));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PenaltyResponse> findAll(Pageable pageable) {
        return penaltyRepository.findAll(pageable).map(PenaltyMapper::toResponse);
    }
}
