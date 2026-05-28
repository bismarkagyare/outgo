package com.outgo.api.domain.expense;

import java.time.Instant;
import java.util.UUID;

import com.outgo.api.domain.shared.DomainEvent;
import com.outgo.api.domain.shared.Money;

public record ExpenseUpdatedEvent(ExpenseId expenseId, UUID userId, Money oldAmount, Money newAmount,
                Category oldCategory, Category newCategory, String oldDescription, String newDescription,
                Instant newExpenseDate, Instant occuredAt)
                implements DomainEvent {

}
