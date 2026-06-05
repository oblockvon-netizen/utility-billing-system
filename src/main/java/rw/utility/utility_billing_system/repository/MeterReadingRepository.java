package rw.utility.utility_billing_system.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import rw.utility.utility_billing_system.entity.MeterReading;

import java.util.UUID;

public interface MeterReadingRepository extends JpaRepository<MeterReading, UUID> {

    boolean existsByMeterIdAndMonthAndYear(UUID meterId, int month, int year);

    Page<MeterReading> findByMeterId(UUID meterId, Pageable pageable);
}
