package rw.utility.utility_billing_system.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.utility.utility_billing_system.dto.TariffRequest;
import rw.utility.utility_billing_system.dto.TariffResponse;
import rw.utility.utility_billing_system.entity.Tariff;
import rw.utility.utility_billing_system.enums.MeterType;
import rw.utility.utility_billing_system.exception.BusinessRuleException;
import rw.utility.utility_billing_system.exception.ResourceNotFoundException;
import rw.utility.utility_billing_system.repository.TariffRepository;
import rw.utility.utility_billing_system.service.TariffService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TariffServiceImpl implements TariffService {

    private final TariffRepository tariffRepository;

    @Override
    @Transactional
    public TariffResponse create(TariffRequest request) {
        validateUnitRange(request);
        Tariff tariff = Tariff.builder().build();
        applyRequest(tariff, request);
        return TariffMapper.toResponse(tariffRepository.save(tariff));
    }

    @Override
    @Transactional
    public TariffResponse update(UUID tariffId, TariffRequest request) {
        validateUnitRange(request);
        Tariff tariff = getTariff(tariffId);
        applyRequest(tariff, request);
        return TariffMapper.toResponse(tariffRepository.save(tariff));
    }

    @Override
    @Transactional(readOnly = true)
    public TariffResponse findById(UUID tariffId) {
        return TariffMapper.toResponse(getTariff(tariffId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TariffResponse> findAll(MeterType meterType, Pageable pageable) {
        Page<Tariff> tariffs = meterType == null
                ? tariffRepository.findAll(pageable)
                : tariffRepository.findByMeterType(meterType, pageable);
        return tariffs.map(TariffMapper::toResponse);
    }

    private Tariff getTariff(UUID tariffId) {
        return tariffRepository.findById(tariffId)
                .orElseThrow(() -> new ResourceNotFoundException("Tariff not found"));
    }

    private void validateUnitRange(TariffRequest request) {
        if (request.minUnits() != null && request.maxUnits() != null
                && request.maxUnits() < request.minUnits()) {
            throw new BusinessRuleException("Maximum units cannot be less than minimum units");
        }
    }

    private void applyRequest(Tariff tariff, TariffRequest request) {
        tariff.setMeterType(request.meterType());
        tariff.setTariffType(request.tariffType());
        tariff.setUnitPrice(request.unitPrice());
        tariff.setMinUnits(request.minUnits());
        tariff.setMaxUnits(request.maxUnits());
        tariff.setServiceCharge(request.serviceCharge());
        tariff.setVatRate(request.vatRate());
        tariff.setPenaltyRate(request.penaltyRate());
        tariff.setEffectiveDate(request.effectiveDate());
        tariff.setActive(request.active());
    }
}
