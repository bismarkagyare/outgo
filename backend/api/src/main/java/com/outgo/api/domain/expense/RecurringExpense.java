package com.outgo.api.domain.expense;

import com.outgo.api.domain.shared.AggregateRoot;
import com.outgo.api.domain.shared.Money;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class RecurringExpense extends AggregateRoot {

    private final RecurringExpenseId id;
    private final UUID userId;
    private Money amount;
    private Category category;
    private String description;
    private Frequency frequency;
    private LocalDate nextRunDate;
    private boolean active;
    private final Instant createdAt;
    private Instant updatedAt;

    private RecurringExpense(
            RecurringExpenseId id,
            UUID userId,
            Money amount,
            Category category,
            String description,
            Frequency frequency,
            LocalDate nextRunDate,
            boolean active,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.frequency = frequency;
        this.nextRunDate = nextRunDate;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static RecurringExpense create(
            UUID userId,
            Money amount,
            Category category,
            String description,
            Frequency frequency,
            LocalDate startDate) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(category, "category must not be null");
        Objects.requireNonNull(frequency, "frequency must not be null");
        Objects.requireNonNull(startDate, "startDate must not be null");

        RecurringExpenseId newId = RecurringExpenseId.generate();
        Instant now = Instant.now();

        RecurringExpense re = new RecurringExpense(
                newId, userId, amount, category, description, frequency, startDate, true, now, now);
        re.registerEvent(new RecurringExpenseCreatedEvent(
                newId.value(), userId, amount, category, description, frequency, startDate, now));
        return re;
    }

    public static RecurringExpense reconstitute(
            RecurringExpenseId id,
            UUID userId,
            Money amount,
            Category category,
            String description,
            Frequency frequency,
            LocalDate nextRunDate,
            boolean active,
            Instant createdAt,
            Instant updatedAt) {
        return new RecurringExpense(
                id, userId, amount, category, description, frequency, nextRunDate, active, createdAt, updatedAt);
    }

    public void advanceNextRunDate() {
        this.nextRunDate = frequency.nextDate(this.nextRunDate);
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
        registerEvent(new RecurringExpenseDeactivatedEvent(id.value(), userId, Instant.now()));
    }

    public RecurringExpenseId getId() {
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

    public Frequency getFrequency() {
        return frequency;
    }

    public LocalDate getNextRunDate() {
        return nextRunDate;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
