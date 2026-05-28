package com.outgo.api.domain.budget;

import java.util.Objects;
import java.util.UUID;

public final class BudgetId {

    private final UUID value;

    private BudgetId(UUID value) {
        this.value = value;
    }

    public static BudgetId generate() {
        return new BudgetId(UUID.randomUUID());
    }

    public static BudgetId of(UUID value) {
        Objects.requireNonNull(value, "BudgetId value must not be null");
        return new BudgetId(value);
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof BudgetId other))
            return false;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
