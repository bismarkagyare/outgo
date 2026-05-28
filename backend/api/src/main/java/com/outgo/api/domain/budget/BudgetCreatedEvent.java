package com.outgo.api.domain.budget;

import com.outgo.api.domain.expense.Category;
import com.outgo.api.domain.shared.DomainEvent;
import com.outgo.api.domain.shared.Money;

import java.time.Instant;
import java.util.UUID;

public record BudgetCreatedEvent(
        BudgetId budgetId,
        UUID userId,
        Money limit,
        Category category,
        int year,
        int month,
        Instant occuredAt) implements DomainEvent {
}
