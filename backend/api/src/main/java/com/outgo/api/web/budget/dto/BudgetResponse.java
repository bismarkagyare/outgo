package com.outgo.api.web.budget.dto;

import com.outgo.api.infrastructure.persistence.readmodel.BudgetSummaryDto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BudgetResponse(
        UUID id,
        BigDecimal amount,
        String currency,
        String category,
        int year,
        int month,
        Instant createdAt,
        Instant updatedAt) {

    public static BudgetResponse from(BudgetSummaryDto dto) {
        return new BudgetResponse(
                dto.id(),
                dto.amount(),
                dto.currency(),
                dto.category(),
                dto.year(),
                dto.month(),
                dto.createdAt(),
                dto.updatedAt());
    }
}
