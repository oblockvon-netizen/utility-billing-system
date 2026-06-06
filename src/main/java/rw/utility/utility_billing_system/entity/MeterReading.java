package rw.utility.utility_billing_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(
        name = "meter_readings",
        // Database enforces one reading per meter per month/year
        uniqueConstraints = @UniqueConstraint(columnNames = {"meter_id", "month", "year"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * JPA entity for monthly meter readings that drive consumption-based billing.
 */
public class MeterReading extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meter_id", nullable = false)
    private Meter meter;

    @Column(nullable = false)
    private Double previousReading;

    @Column(nullable = false)
    private Double currentReading;

    @Column(nullable = false)
    private Double consumption;

    @Column(nullable = false)
    private LocalDate readingDate;

    @Column(nullable = false)
    private int month;

    @Column(nullable = false)
    private int year;
}
