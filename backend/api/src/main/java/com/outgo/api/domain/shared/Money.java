package com.outgo.api.domain.shared;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Money {
    private final BigDecimal amount;
    private final String currency;

    private Money(BigDecimal amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public static Money of(BigDecimal amount, String currency) {
        Objects.requireNonNull(amount, "Money amount must not be null");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Expense amount must be greater than zero, was: " + amount);
        }

        Objects.requireNonNull(currency, "Currency code must not be null");

        if (currency.isBlank()) {
            throw new IllegalArgumentException("Currency code must not be blank");
        }

        BigDecimal scaled = amount.setScale(2, RoundingMode.HALF_UP);

        String normalisedCurrency = currency.toUpperCase();

        return new Money(scaled, normalisedCurrency);
    }

    public Money add(Money other) {
        Objects.requireNonNull(other, "Cannot add null to Money");

        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                    "Cannot add money of different currencies: " + this.currency + " + " + other.currency);
        }

        return new Money(this.amount.add(other.amount).setScale(2, RoundingMode.HALF_UP), this.currency);
    }

    public boolean isGreaterThan(Money other) {
        Objects.requireNonNull(other, "Cannot compare with null Money");

        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                    "Cannot compare money of different currencies: " + this.currency + " vs " + other.currency);
        }

        return this.amount.compareTo(other.amount) > 0;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true; 
        if (!(o instanceof Money m))
            return false; 
        return amount.compareTo(m.amount) == 0 
                && currency.equals(m.currency); 
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount.stripTrailingZeros(), currency);
    }

    @Override
    public String toString() {
        return amount.toPlainString() + " " + currency;
    }

}
