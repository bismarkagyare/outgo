package com.outgo.api.domain.budget;

import com.outgo.api.domain.expense.Category;
import com.outgo.api.domain.shared.AggregateRoot;
import com.outgo.api.domain.shared.Money;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Budget extends AggregateRoot {

    private final BudgetId id;
    private final UUID userId;
    private Money limit;
    private final Category category;
    private final int year;
    private final int month;
    private final Instant createdAt;
    private Instant updatedAt;

    private Budget(BudgetId id, UUID userId, Money limit, Category category,
            int year, int month, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.limit = limit;
        this.category = category;
        this.year = year;
        this.month = month;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Budget create(UUID userId, Money limit, Category category, int year, int month) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(limit, "limit must not be null");
        if (year < 2000 || year > 2100)
            throw new IllegalArgumentException("Invalid year: " + year);
        if (month < 1 || month > 12)
            throw new IllegalArgumentException("Invalid month: " + month);

        BudgetId newId = BudgetId.generate();
        Instant now = Instant.now();
        Budget budget = new Budget(newId, userId, limit, category, year, month, now, now);
        budget.registerEvent(new BudgetCreatedEvent(newId, userId, limit, category, year, month, now));
        return budget;
    }

    public static Budget reconstitute(BudgetId id, UUID userId, Money limit, Category category,
            int year, int month, Instant createdAt, Instant updatedAt) {
        return new Budget(id, userId, limit, category, year, month, createdAt, updatedAt);
    }

    public void update(Money newLimit) {
        Objects.requireNonNull(newLimit, "newLimit must not be null");
        Money oldLimit = this.limit;
        this.limit = newLimit;
        this.updatedAt = Instant.now();
        registerEvent(new BudgetUpdatedEvent(id, userId, oldLimit, newLimit, this.updatedAt));
    }

    public BudgetId getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public Money getLimit() {
        return limit;
    }

    public Category getCategory() {
        return category;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Budget other))
            return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
