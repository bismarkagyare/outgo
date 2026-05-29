package com.outgo.api.domain.expense;

import java.time.LocalDate;

public enum Frequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY;

    public LocalDate nextDate(LocalDate current) {
        return switch (this) {
            case DAILY -> current.plusDays(1);
            case WEEKLY -> current.plusWeeks(1);
            case MONTHLY -> current.plusMonths(1);
            case YEARLY -> current.plusYears(1);
        };
    }
}
