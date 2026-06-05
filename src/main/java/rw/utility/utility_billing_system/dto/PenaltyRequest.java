package rw.utility.utility_billing_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Request body for creating a penalty rule.
 */
public record PenaltyRequest(
        @NotBlank(message = "Name is required")
        String name,

        String description,

        @NotNull(message = "Penalty rate is required")
        @PositiveOrZero(message = "Penalty rate cannot be negative")
        Double rate,

        @NotNull(message = "Days after due date is required")
        @Positive(message = "Days after due date must be greater than zero")
        Integer daysAfterDue
) {
}
