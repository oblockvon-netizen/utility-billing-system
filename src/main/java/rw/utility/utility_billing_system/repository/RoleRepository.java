package rw.utility.utility_billing_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data repository for Role lookup by enum name.
 */

public interface RoleRepository extends JpaRepository<rw.utility.utility_billing_system.entity.Role, UUID> {

    // Resolves enum constants to persisted role rows
    Optional<rw.utility.utility_billing_system.entity.Role> findByName(
            rw.utility.utility_billing_system.enums.Role name
    );
}
