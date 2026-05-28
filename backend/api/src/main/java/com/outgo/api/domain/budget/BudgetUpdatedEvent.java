package com.outgo.api.domain.budget;

import com.outgo.api.domain.shared.DomainEvent;
import com.outgo.api.domain.shared.Money;

import java.time.Instant;
import java.util.UUID;

public record BudgetUpdatedEvent(
        BudgetId budgetId,
        UUID userId,
        Money oldLimit,
        Money newLimit,
        Instant occuredAt) implements DomainEvent {
}
