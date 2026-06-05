package rw.utility.utility_billing_system.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import rw.utility.utility_billing_system.entity.Meter;

import java.util.UUID;

public interface MeterRepository extends JpaRepository<Meter, UUID> {

    boolean existsByMeterNumber(String meterNumber);

    Page<Meter> findByCustomerId(UUID customerId, Pageable pageable);
}
