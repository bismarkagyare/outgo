package com.outgo.api.infrastructure.persistence.readmodel;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record RecurringExpenseSummaryDto(
        UUID id,
        BigDecimal amount,
        String currency,
        String category,
        String description,
        String frequency,
        LocalDate nextRunDate,
        boolean active,
        Instant createdAt,
        Instant updatedAt) {
}
