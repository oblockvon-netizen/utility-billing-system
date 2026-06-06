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
import rw.utility.utility_billing_system.dto.MeterRequest;
import rw.utility.utility_billing_system.dto.MeterResponse;
import rw.utility.utility_billing_system.dto.StatusUpdateRequest;
import rw.utility.utility_billing_system.service.MeterService;

import java.util.UUID;

@RestController
@RequestMapping("/api/meters")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OPERATOR')")
@Tag(name = "Meters", description = "Water and electricity meter management")
/**
 * REST resource for water/electricity meter registration and lifecycle. Restricted to ADMIN and OPERATOR roles.
 */
public class MeterController {

    private final MeterService meterService;

    @PostMapping
    public ResponseEntity<ApiResponse<MeterResponse>> create(
            @Valid @RequestBody MeterRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Meter created successfully",
                        meterService.create(request)
                ));
    }

    @PutMapping("/{meterId}")
    public ResponseEntity<ApiResponse<MeterResponse>> update(
            @PathVariable UUID meterId,
            @Valid @RequestBody MeterRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Meter updated successfully",
                meterService.update(meterId, request)
        ));
    }

    @GetMapping("/{meterId}")
    public ResponseEntity<ApiResponse<MeterResponse>> findById(@PathVariable UUID meterId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Meter retrieved successfully",
                meterService.findById(meterId)
        ));
    }

    // Lists all meters registered to a single customer
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<Page<MeterResponse>>> findByCustomer(
            @PathVariable UUID customerId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Customer meters retrieved successfully",
                meterService.findByCustomer(customerId, pageable)
        ));
    }

    @PutMapping("/{meterId}/status")
    public ResponseEntity<ApiResponse<MeterResponse>> updateStatus(
            @PathVariable UUID meterId,
            @Valid @RequestBody StatusUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Meter status updated successfully",
                meterService.updateStatus(meterId, request.status())
        ));
    }
}
