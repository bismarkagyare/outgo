package com.outgo.api.domain.expense;

import java.time.Instant;
import java.util.UUID;

import com.outgo.api.domain.shared.DomainEvent;

public record ExpenseDeletedEvent(ExpenseId expenseId, UUID userId, Instant occuredAt) implements DomainEvent {

}
