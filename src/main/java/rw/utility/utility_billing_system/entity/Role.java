package rw.utility.utility_billing_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * JPA entity for the roles lookup table referenced by users.
 */
public class Role extends BaseEntity {

    @Enumerated(EnumType.STRING)
    // One row per Role enum value, seeded on startup
    @Column(nullable = false, unique = true)
    private rw.utility.utility_billing_system.enums.Role name;
}
