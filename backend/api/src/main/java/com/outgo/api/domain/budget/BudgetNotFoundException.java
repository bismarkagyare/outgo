package com.outgo.api.domain.budget;

import java.util.UUID;

public class BudgetNotFoundException extends RuntimeException {

    public BudgetNotFoundException(BudgetId id, UUID userId) {
        super("Budget not found: id=" + id + ", userId=" + userId);
    }
}
