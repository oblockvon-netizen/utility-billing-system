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
import org.springframework.web.bind.annotation.RestController;
import rw.utility.utility_billing_system.dto.ApiResponse;
import rw.utility.utility_billing_system.dto.MeterReadingRequest;
import rw.utility.utility_billing_system.dto.MeterReadingResponse;
import rw.utility.utility_billing_system.service.MeterReadingService;

import java.util.UUID;

@RestController
@RequestMapping("/api/readings")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('ROLE_OPERATOR')")
@Tag(name = "Meter Readings", description = "Monthly utility meter reading capture and retrieval")
public class ReadingController {

    private final MeterReadingService meterReadingService;

    @PostMapping
    public ResponseEntity<ApiResponse<MeterReadingResponse>> create(
            @Valid @RequestBody MeterReadingRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Meter reading captured successfully",
                        meterReadingService.create(request)
                ));
    }

    @GetMapping("/{readingId}")
    public ResponseEntity<ApiResponse<MeterReadingResponse>> findById(
            @PathVariable UUID readingId
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Meter reading retrieved successfully",
                meterReadingService.findById(readingId)
        ));
    }

    @GetMapping("/meter/{meterId}")
    public ResponseEntity<ApiResponse<Page<MeterReadingResponse>>> findByMeter(
            @PathVariable UUID meterId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Meter readings retrieved successfully",
                meterReadingService.findByMeter(meterId, pageable)
        ));
    }
}
