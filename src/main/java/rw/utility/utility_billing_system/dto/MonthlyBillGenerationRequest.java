package rw.utility.utility_billing_system.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record MonthlyBillGenerationRequest(
        @Min(value = 1, message = "Month must be between 1 and 12")
        @Max(value = 12, message = "Month must be between 1 and 12")
        int month,

        @Min(value = 2000, message = "Year must be 2000 or later")
        int year
) {
}
