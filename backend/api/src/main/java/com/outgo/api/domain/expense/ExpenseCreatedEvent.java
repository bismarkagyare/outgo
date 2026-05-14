package com.outgo.api.domain.expense;

import java.time.Instant;
import java.util.UUID;

import com.outgo.api.domain.shared.DomainEvent;
import com.outgo.api.domain.shared.Money;

public record ExpenseCreatedEvent(ExpenseId expenseId, UUID userId, Money amount, Category category, String description,
        Instant expenseDate, Instant occuredAt) implements DomainEvent {

}
