package com.outgo.api.domain.budget;

import com.outgo.api.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record BudgetDeletedEvent(
        BudgetId budgetId,
        UUID userId,
        Instant occuredAt) implements DomainEvent {
}
