package rw.utility.utility_billing_system.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rw.utility.utility_billing_system.dto.PenaltyRequest;
import rw.utility.utility_billing_system.dto.PenaltyResponse;

import java.util.UUID;

/**
 * Business contract for admin penalty rule management.
 */
public interface PenaltyService {

    PenaltyResponse create(PenaltyRequest request);

    PenaltyResponse toggleActive(UUID penaltyId);

    Page<PenaltyResponse> findAll(Pageable pageable);
}
