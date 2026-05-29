package com.outgo.api.domain.expense;

import java.util.UUID;

public record RecurringExpenseId(UUID value) {

    public static RecurringExpenseId generate() {
        return new RecurringExpenseId(UUID.randomUUID());
    }

    public static RecurringExpenseId of(UUID value) {
        return new RecurringExpenseId(value);
    }
}
