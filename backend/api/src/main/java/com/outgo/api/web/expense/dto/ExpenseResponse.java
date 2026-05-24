package com.outgo.api.web.expense.dto;

import com.outgo.api.infrastructure.persistence.readmodel.ExpenseSummaryDto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ExpenseResponse(
        UUID id,
        BigDecimal amount,
        String currency,
        String category,
        String description,
        Instant expenseDate,
        Instant createdAt,
        Instant updatedAt) {

    public static ExpenseResponse from(ExpenseSummaryDto dto) {
        return new ExpenseResponse(
                dto.id(),
                dto.amount(),
                dto.currency(),
                dto.category(),
                dto.description(),
                dto.expenseDate(),
                dto.createdAt(),
                dto.updatedAt());
    }
}
