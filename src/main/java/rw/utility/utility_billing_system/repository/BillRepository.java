package rw.utility.utility_billing_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.utility.utility_billing_system.entity.Bill;

import java.util.UUID;

/**
 * Spring Data repository for Bill persistence and existence checks by reading.
 */

public interface BillRepository extends JpaRepository<Bill, UUID> {

    // Prevents duplicate bills for the same meter reading
    boolean existsByMeterReadingId(UUID meterReadingId);}
