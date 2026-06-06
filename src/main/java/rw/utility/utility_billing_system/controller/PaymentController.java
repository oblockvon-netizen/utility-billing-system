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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rw.utility.utility_billing_system.dto.ApiResponse;
import rw.utility.utility_billing_system.dto.PaymentRequest;
import rw.utility.utility_billing_system.dto.PaymentResponse;
import rw.utility.utility_billing_system.service.PaymentService;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyAuthority('ROLE_FINANCE', 'ROLE_CUSTOMER')")
@Tag(name = "Payments", description = "Utility bill payment recording and retrieval")
/**
 * REST resource for recording bill payments and listing payment history. Accessible to FINANCE and CUSTOMER roles.
 */
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> recordPayment(
            @Valid @RequestBody PaymentRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Payment recorded successfully",
                        paymentService.recordPayment(request)
                ));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> findById(
            @PathVariable UUID paymentId
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Payment retrieved successfully",
                paymentService.findById(paymentId)
        ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> findAll(
            // Optional filter to list payments for one bill
            @RequestParam(required = false) UUID billId,            Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Payments retrieved successfully",
                paymentService.findAll(billId, pageable)
        ));
    }
}
