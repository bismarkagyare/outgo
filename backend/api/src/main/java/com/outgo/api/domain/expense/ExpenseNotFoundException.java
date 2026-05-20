package com.outgo.api.domain.expense;

import java.util.UUID;

public class ExpenseNotFoundException extends RuntimeException {

    public ExpenseNotFoundException(ExpenseId id, UUID userId) {
        super("Expense not found: id=" + id + ", userId=" + userId);
    }
}
