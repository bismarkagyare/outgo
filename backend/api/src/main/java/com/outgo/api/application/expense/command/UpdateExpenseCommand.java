package com.outgo.api.application.expense.command;

import com.outgo.api.domain.expense.Category;
import com.outgo.api.domain.expense.ExpenseId;
import com.outgo.api.domain.shared.Money;

import java.time.Instant;
import java.util.UUID;

public record UpdateExpenseCommand(
        ExpenseId expenseId,
        UUID userId,
        Money newAmount,
        Category newCategory,
        String newDescription,
        Instant newExpenseDate) {
}
