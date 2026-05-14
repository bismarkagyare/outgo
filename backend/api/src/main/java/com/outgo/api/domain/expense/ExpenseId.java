package com.outgo.api.domain.expense;

import java.util.Objects;
import java.util.UUID;

public final class ExpenseId {
    private final UUID value;

    private ExpenseId(UUID value) {
        this.value = value;
    }

    public static ExpenseId generate() {
        return new ExpenseId(UUID.randomUUID());
    }

    public static ExpenseId of(UUID value) {
        Objects.requireNonNull(value, "Expense value must not be null");
        return new ExpenseId(value);
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ExpenseId e))
            return false;
        return Objects.equals(value, e.value);
    }
}
