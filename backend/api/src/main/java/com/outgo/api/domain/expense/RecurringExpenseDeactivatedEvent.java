package com.outgo.api.domain.expense;

import com.outgo.api.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record RecurringExpenseDeactivatedEvent(
        UUID recurringExpenseId,
        UUID userId,
        Instant occuredAt) implements DomainEvent {
}
