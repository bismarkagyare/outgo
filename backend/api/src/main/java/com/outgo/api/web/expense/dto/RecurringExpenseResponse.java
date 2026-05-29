package com.outgo.api.web.expense.dto;

import com.outgo.api.infrastructure.persistence.readmodel.RecurringExpenseSummaryDto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record RecurringExpenseResponse(
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

    public static RecurringExpenseResponse from(RecurringExpenseSummaryDto dto) {
        return new RecurringExpenseResponse(
                dto.id(),
                dto.amount(),
                dto.currency(),
                dto.category(),
                dto.description(),
                dto.frequency(),
                dto.nextRunDate(),
                dto.active(),
                dto.createdAt(),
                dto.updatedAt());
    }
}
