package rw.utility.utility_billing_system.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.utility.utility_billing_system.dto.BillApprovalRequest;
import rw.utility.utility_billing_system.dto.BillResponse;
import rw.utility.utility_billing_system.entity.Bill;
import rw.utility.utility_billing_system.entity.Customer;
import rw.utility.utility_billing_system.entity.Meter;
import rw.utility.utility_billing_system.entity.MeterReading;
import rw.utility.utility_billing_system.entity.Tariff;
import rw.utility.utility_billing_system.enums.BillStatus;
import rw.utility.utility_billing_system.enums.Status;
import rw.utility.utility_billing_system.exception.BusinessRuleException;
import rw.utility.utility_billing_system.exception.DuplicateResourceException;
import rw.utility.utility_billing_system.exception.ResourceNotFoundException;
import rw.utility.utility_billing_system.repository.BillRepository;
import rw.utility.utility_billing_system.repository.MeterReadingRepository;
import rw.utility.utility_billing_system.repository.TariffRepository;
import rw.utility.utility_billing_system.service.BillService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
/**
 * Generates bills from meter readings using applicable tariffs; supports approval and batch generation.
 */
public class BillServiceImpl implements BillService {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    private static final int MONEY_SCALE = 2;

    private final BillRepository billRepository;
    private final MeterReadingRepository meterReadingRepository;
    private final TariffRepository tariffRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public BillResponse generateBill(UUID readingId) {
        MeterReading reading = meterReadingRepository.findById(readingId)
                .orElseThrow(() -> new ResourceNotFoundException("Meter reading not found"));
        if (billRepository.existsByMeterReadingId(readingId)) {
            throw new DuplicateResourceException("A bill already exists for this meter reading");
        }

        Meter meter = reading.getMeter();
        Customer customer = meter.getCustomer();
        if (customer.getStatus() != Status.ACTIVE) {
            throw new BusinessRuleException("Inactive customers cannot receive new bills");
        }
        if (meter.getStatus() != Status.ACTIVE) {
            throw new BusinessRuleException("Inactive meters cannot be billed");
        }

        LocalDate generationDate = LocalDate.now();
        Tariff tariff = tariffRepository
                // Select the most recent active tariff effective on the bill date
                .findTopByMeterTypeAndActiveTrueAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(
                        meter.getMeterType(),
                        generationDate
                )
                .orElseThrow(() -> new BusinessRuleException(
                        "No active applicable tariff exists for this meter type"
                ));

        BigDecimal consumption = BigDecimal.valueOf(reading.getConsumption());
        BigDecimal unitCharge = money(consumption.multiply(tariff.getUnitPrice()));
        BigDecimal serviceCharge = money(tariff.getServiceCharge());
        BigDecimal vatAmount = money(
                unitCharge.add(serviceCharge)
                        .multiply(BigDecimal.valueOf(tariff.getVatRate()))
                        .divide(ONE_HUNDRED, MONEY_SCALE, RoundingMode.HALF_UP)
        );
        BigDecimal totalAmount = money(unitCharge.add(serviceCharge).add(vatAmount));

        Bill bill = Bill.builder()
                .customer(customer)
                .meter(meter)
                .meterReading(reading)
                .month(reading.getMonth())
                .year(reading.getYear())
                .totalConsumption(reading.getConsumption())
                .unitCharge(unitCharge)
                .serviceCharge(serviceCharge)
                .vatAmount(vatAmount)
                .penaltyAmount(money(BigDecimal.ZERO))
                .totalAmount(totalAmount)
                .amountPaid(money(BigDecimal.ZERO))
                .outstandingBalance(totalAmount)
                .status(BillStatus.PENDING)
                .generatedDate(generationDate)
                .dueDate(generationDate.plusDays(30))
                .build();
        return BillMapper.toResponse(billRepository.save(bill));
    }

    @Override
    @Transactional
    public void generateMonthlyBills(int month, int year) {
        jdbcTemplate.update("CALL generate_monthly_bills(?, ?)", month, year);
    }

    @Override
    @Transactional
    public BillResponse approve(UUID billId, BillApprovalRequest request) {
        if (request.status() != BillStatus.APPROVED) {
            throw new BusinessRuleException("Bill approval can only change status to APPROVED");
        }

        Bill bill = getBill(billId);
        if (bill.getStatus() != BillStatus.PENDING) {
            throw new BusinessRuleException("Only PENDING bills can be approved");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessRuleException("Authenticated approver is required");
        }

        bill.setStatus(BillStatus.APPROVED);
        bill.setApprovedBy(authentication.getName());
        return BillMapper.toResponse(billRepository.save(bill));
    }

    @Override
    @Transactional(readOnly = true)
    public BillResponse findById(UUID billId) {
        return BillMapper.toResponse(getBill(billId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BillResponse> findAll(Pageable pageable) {
        return billRepository.findAll(pageable).map(BillMapper::toResponse);
    }

    private Bill getBill(UUID billId) {
        return billRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found"));
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }
}
