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
import rw.utility.utility_billing_system.dto.CustomerRequest;
import rw.utility.utility_billing_system.dto.CustomerResponse;
import rw.utility.utility_billing_system.dto.StatusUpdateRequest;
import rw.utility.utility_billing_system.service.CustomerService;

import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OPERATOR')")
@Tag(name = "Customers", description = "Utility customer management")
/**
 * REST resource for utility customer CRUD and status changes. Restricted to ADMIN and OPERATOR roles.
 */
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponse>> create(
            @Valid @RequestBody CustomerRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Customer created successfully",
                        customerService.create(request)
                ));
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<ApiResponse<CustomerResponse>> update(
            @PathVariable UUID customerId,
            @Valid @RequestBody CustomerRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Customer updated successfully",
                customerService.update(customerId, request)
        ));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<CustomerResponse>> findById(
            @PathVariable UUID customerId
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Customer retrieved successfully",
                customerService.findById(customerId)
        ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CustomerResponse>>> findAll(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                "Customers retrieved successfully",
                customerService.findAll(pageable)
        ));
    }

    // Deactivating a customer blocks new bills on their meters
    @PutMapping("/{customerId}/status")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateStatus(
            @PathVariable UUID customerId,
            @Valid @RequestBody StatusUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Customer status updated successfully",
                customerService.updateStatus(customerId, request.status())
        ));
    }
}
