package rw.utility.utility_billing_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "penalties")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * Configurable late-payment penalty rule applied to overdue bills.
 */
public class Penalty extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Percentage rate applied to the outstanding balance
    @Column(nullable = false)
    private Double rate;

    // Days after the bill due date before this penalty applies
    @Column(nullable = false)
    private Integer daysAfterDue;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}
