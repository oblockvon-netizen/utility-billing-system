package rw.utility.utility_billing_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.utility.utility_billing_system.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
