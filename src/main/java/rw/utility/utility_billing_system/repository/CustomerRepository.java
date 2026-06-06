package rw.utility.utility_billing_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.utility.utility_billing_system.entity.Customer;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data repository for Customer CRUD and lookup queries.
 */

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    // Supports unique national ID validation on create/update
    boolean existsByNationalId(String nationalId);
    Optional<Customer> findByEmailIgnoreCase(String email);
}
