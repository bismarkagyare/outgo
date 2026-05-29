package com.outgo.api.application.expense.command;

import com.outgo.api.domain.expense.Category;
import com.outgo.api.domain.expense.Frequency;
import com.outgo.api.domain.shared.Money;

import java.time.LocalDate;
import java.util.UUID;

public record CreateRecurringExpenseCommand(
        UUID userId,
        Money amount,
        Category category,
        String description,
        Frequency frequency,
        LocalDate startDate) {
}
