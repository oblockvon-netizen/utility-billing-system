package rw.utility.utility_billing_system.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rw.utility.utility_billing_system.dto.TariffRequest;
import rw.utility.utility_billing_system.dto.TariffResponse;
import rw.utility.utility_billing_system.enums.MeterType;

import java.util.UUID;

public interface TariffService {

    TariffResponse create(TariffRequest request);

    TariffResponse update(UUID tariffId, TariffRequest request);

    TariffResponse findById(UUID tariffId);

    Page<TariffResponse> findAll(MeterType meterType, Pageable pageable);
}
