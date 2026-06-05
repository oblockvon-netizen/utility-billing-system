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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rw.utility.utility_billing_system.dto.ApiResponse;
import rw.utility.utility_billing_system.dto.PenaltyRequest;
import rw.utility.utility_billing_system.dto.PenaltyResponse;
import rw.utility.utility_billing_system.service.PenaltyService;

import java.util.UUID;

@RestController
@RequestMapping({"/api/penalties", "/api/penalities"})
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Penalties", description = "Late-payment penalty rule configuration")
/**
 * REST resource for penalty rule management. Restricted to ADMIN role.
 */
public class PenaltyController {

    private final PenaltyService penaltyService;

    @PostMapping
    public ResponseEntity<ApiResponse<PenaltyResponse>> create(
            @Valid @RequestBody PenaltyRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Penalty rule created successfully", penaltyService.create(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PenaltyResponse>>> findAll(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                "Penalty rules retrieved successfully",
                penaltyService.findAll(pageable)
        ));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<PenaltyResponse>> toggleActive(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Penalty rule status toggled successfully",
                penaltyService.toggleActive(id)
        ));
    }
}
