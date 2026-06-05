package rw.utility.utility_billing_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.utility.utility_billing_system.entity.Penalty;

import java.util.UUID;

/**
 * Spring Data repository for penalty rule configuration.
 */
public interface PenaltyRepository extends JpaRepository<Penalty, UUID> {
}
