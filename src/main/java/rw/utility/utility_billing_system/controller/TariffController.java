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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rw.utility.utility_billing_system.dto.ApiResponse;
import rw.utility.utility_billing_system.dto.TariffRequest;
import rw.utility.utility_billing_system.dto.TariffResponse;
import rw.utility.utility_billing_system.enums.MeterType;
import rw.utility.utility_billing_system.service.TariffService;

import java.util.UUID;

@RestController
@RequestMapping("/api/tariffs")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Tariffs", description = "Water and electricity tariff configuration")
/**
 * REST resource for tariff configuration (unit price, VAT, service charge). Restricted to ADMIN role.
 */
public class TariffController {

    private final TariffService tariffService;

    @PostMapping
    public ResponseEntity<ApiResponse<TariffResponse>> create(
            @Valid @RequestBody TariffRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tariff created successfully", tariffService.create(request)));
    }

    @PutMapping("/{tariffId}")
    public ResponseEntity<ApiResponse<TariffResponse>> update(
            @PathVariable UUID tariffId,
            @Valid @RequestBody TariffRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Tariff updated successfully",
                tariffService.update(tariffId, request)
        ));
    }

    @GetMapping("/{tariffId}")
    public ResponseEntity<ApiResponse<TariffResponse>> findById(@PathVariable UUID tariffId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Tariff retrieved successfully",
                tariffService.findById(tariffId)
        ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TariffResponse>>> findAll(
            // Omit to return tariffs for all meter types
            @RequestParam(required = false) MeterType meterType,            Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Tariffs retrieved successfully",
                tariffService.findAll(meterType, pageable)
        ));
    }
}
