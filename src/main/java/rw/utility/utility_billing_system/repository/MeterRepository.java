package rw.utility.utility_billing_system.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import rw.utility.utility_billing_system.entity.Meter;

import java.util.UUID;

/**
 * Spring Data repository for Meter persistence and customer-scoped queries.
 */

public interface MeterRepository extends JpaRepository<Meter, UUID> {

    boolean existsByMeterNumber(String meterNumber);

    // Lists meters registered under a given customer
    Page<Meter> findByCustomerId(UUID customerId, Pageable pageable);}
