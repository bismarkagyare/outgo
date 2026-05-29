package com.outgo.api.domain.expense;

import com.outgo.api.domain.shared.DomainEvent;
import com.outgo.api.domain.shared.Money;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record RecurringExpenseCreatedEvent(
        UUID recurringExpenseId,
        UUID userId,
        Money amount,
        Category category,
        String description,
        Frequency frequency,
        LocalDate startDate,
        Instant occuredAt) implements DomainEvent {
}
