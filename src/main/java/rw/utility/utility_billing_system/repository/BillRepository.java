package rw.utility.utility_billing_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.utility.utility_billing_system.entity.Bill;

import java.util.UUID;

public interface BillRepository extends JpaRepository<Bill, UUID> {

    boolean existsByMeterReadingId(UUID meterReadingId);
}
