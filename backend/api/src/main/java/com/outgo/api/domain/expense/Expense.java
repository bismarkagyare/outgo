package com.outgo.api.domain.expense;

import com.outgo.api.domain.shared.AggregateRoot;
import com.outgo.api.domain.shared.Money;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Expense extends AggregateRoot {

    private final ExpenseId id;
    private final UUID userId;

    private Money amount;
    private Category category;
    private String description;
    private Instant expenseDate;

    private final Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    private Expense(
            ExpenseId id,
            UUID userId,
            Money amount,
            Category category,
            String description,
            Instant expenseDate,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.expenseDate = expenseDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static Expense create(
            UUID userId,
            Money amount,
            Category category,
            String description,
            Instant expenseDate) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(category, "category must not be null");
        Objects.requireNonNull(expenseDate, "expenseDate must not be null");

        ExpenseId newId = ExpenseId.generate();
        Instant now = Instant.now();

        Expense expense = new Expense(
                newId,
                userId,
                amount,
                category,
                description,
                expenseDate,
                now,
                now,
                null);

        expense.registerEvent(new ExpenseCreatedEvent(
                newId,
                userId,
                amount,
                category,
                description,
                expenseDate,
                now));

        return expense;
    }

    public static Expense reconstitute(
            ExpenseId id,
            UUID userId,
            Money amount,
            Category category,
            String description,
            Instant expenseDate,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new Expense(id, userId, amount, category, description, expenseDate, createdAt, updatedAt, deletedAt);
    }

    public void update(
            Money newAmount,
            Category newCategory,
            String newDescription,
            Instant newExpenseDate) {
        if (this.deletedAt != null) {
            throw new IllegalStateException(
                    "Cannot update a deleted expense. expenseId=" + this.id + ", deletedAt=" + this.deletedAt);
        }

        Objects.requireNonNull(newAmount, "newAmount must not be null");
        Objects.requireNonNull(newCategory, "newCategory must not be null");
        Objects.requireNonNull(newExpenseDate, "newExpenseDate must not be null");

        Money oldAmount = this.amount;
        Category oldCategory = this.category;
        String oldDescription = this.description;

        this.amount = newAmount;
        this.category = newCategory;
        this.description = newDescription;
        this.expenseDate = newExpenseDate;
        this.updatedAt = Instant.now();

        this.registerEvent(new ExpenseUpdatedEvent(
                this.id,
                this.userId,
                oldAmount,
                newAmount,
                oldCategory,
                newCategory,
                oldDescription,
                newDescription,
                newExpenseDate,
                this.updatedAt));
    }

    public void delete() {
        if (this.deletedAt != null) {
            throw new IllegalStateException(
                    "Expense is already deleted. expenseId=" + this.id + ", deletedAt=" + this.deletedAt);
        }

        Instant now = Instant.now();

        this.deletedAt = now;
        this.updatedAt = now;

        this.registerEvent(new ExpenseDeletedEvent(this.id, this.userId, now));
    }

    public ExpenseId getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public Money getAmount() {
        return amount;
    }

    public Category getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public Instant getExpenseDate() {
        return expenseDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Expense e))
            return false;
        return Objects.equals(id, e.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}