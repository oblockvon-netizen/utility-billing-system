package rw.utility.utility_billing_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.utility.utility_billing_system.entity.Customer;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    boolean existsByNationalId(String nationalId);
}
