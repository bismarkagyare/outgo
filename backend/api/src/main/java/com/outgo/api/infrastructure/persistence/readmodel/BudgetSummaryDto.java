package com.outgo.api.infrastructure.persistence.readmodel;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BudgetSummaryDto(
        UUID id,
        BigDecimal amount,
        String currency,
        String category,
        int year,
        int month,
        Instant createdAt,
        Instant updatedAt) {
}
