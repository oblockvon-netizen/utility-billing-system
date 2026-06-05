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
import rw.utility.utility_billing_system.enums.MeterType;
import rw.utility.utility_billing_system.enums.TariffType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tariffs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tariff extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeterType meterType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TariffType tariffType;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;

    private Double minUnits;

    private Double maxUnits;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal serviceCharge;

    @Column(nullable = false)
    private Double vatRate;

    @Column(nullable = false)
    private Double penaltyRate;

    @Column(nullable = false)
    private LocalDate effectiveDate;

    @Column(nullable = false)
    private boolean active;
}
