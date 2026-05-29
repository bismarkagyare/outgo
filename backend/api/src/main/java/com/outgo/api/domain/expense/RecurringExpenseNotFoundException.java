package com.outgo.api.domain.expense;

import java.util.UUID;

public class RecurringExpenseNotFoundException extends RuntimeException {
    public RecurringExpenseNotFoundException(UUID id, UUID userId) {
        super("Recurring expense not found: id=" + id + ", userId=" + userId);
    }
}
