package com.outgo.api.application.budget;

import com.outgo.api.domain.budget.BudgetId;
import com.outgo.api.domain.shared.Money;

import java.util.UUID;

public record UpdateBudgetCommand(BudgetId budgetId, UUID userId, Money newLimit) {
}
