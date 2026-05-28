package com.outgo.api.application.budget;

import com.outgo.api.domain.expense.Category;
import com.outgo.api.domain.shared.Money;

import java.util.UUID;

public record CreateBudgetCommand(UUID userId, Money limit, Category category, int year, int month) {
}
