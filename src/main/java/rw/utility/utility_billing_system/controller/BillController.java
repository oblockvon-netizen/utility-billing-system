package rw.utility.utility_billing_system.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rw.utility.utility_billing_system.dto.ApiResponse;
import rw.utility.utility_billing_system.dto.BillApprovalRequest;
import rw.utility.utility_billing_system.dto.BillResponse;
import rw.utility.utility_billing_system.dto.MonthlyBillGenerationRequest;
import rw.utility.utility_billing_system.service.BillService;

import java.util.UUID;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_FINANCE', 'ROLE_CUSTOMER')")
@Tag(name = "Bills", description = "Utility bill generation, approval, and retrieval")
public class BillController {

    private final BillService billService;

    @PostMapping("/generate/{readingId}")
    public ResponseEntity<ApiResponse<BillResponse>> generateBill(
            @PathVariable UUID readingId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Bill generated successfully",
                        billService.generateBill(readingId)
                ));
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> generateMonthlyBills(
            @Valid @RequestBody MonthlyBillGenerationRequest request
    ) {
        billService.generateMonthlyBills(request.month(), request.year());
        return ResponseEntity.ok(ApiResponse.success(
                "Monthly bill generation completed",
                null
        ));
    }

    @PutMapping("/{billId}/approve")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_FINANCE')")
    public ResponseEntity<ApiResponse<BillResponse>> approve(
            @PathVariable UUID billId,
            @Valid @RequestBody BillApprovalRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Bill approved successfully",
                billService.approve(billId, request)
        ));
    }

    @GetMapping("/{billId}")
    public ResponseEntity<ApiResponse<BillResponse>> findById(@PathVariable UUID billId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Bill retrieved successfully",
                billService.findById(billId)
        ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BillResponse>>> findAll(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                "Bills retrieved successfully",
                billService.findAll(pageable)
        ));
    }
}
