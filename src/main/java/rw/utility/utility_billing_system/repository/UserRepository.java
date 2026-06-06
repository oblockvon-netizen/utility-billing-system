package rw.utility.utility_billing_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.utility.utility_billing_system.entity.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data repository for User lookup by email and existence checks.
 */

public interface UserRepository extends JpaRepository<User, UUID> {

    // Email is the login username for authentication
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
