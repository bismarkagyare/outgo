package com.outgo.api.application.expense.command;

import com.outgo.api.domain.expense.Category;
import com.outgo.api.domain.shared.Money;

import java.time.Instant;
import java.util.UUID;

public record CreateExpenseCommand(
        UUID userId,
        Money amount,
        Category category,
        String description,
        Instant expenseDate) {
}
