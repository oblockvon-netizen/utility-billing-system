package rw.utility.utility_billing_system.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import rw.utility.utility_billing_system.entity.Tariff;
import rw.utility.utility_billing_system.enums.MeterType;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface TariffRepository extends JpaRepository<Tariff, UUID> {

    Optional<Tariff> findTopByMeterTypeAndActiveTrueAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(
            MeterType type,
            LocalDate date
    );

    Page<Tariff> findByMeterType(MeterType meterType, Pageable pageable);
}
