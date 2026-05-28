package com.outgo.api.domain.budget;

import com.outgo.api.domain.expense.Category;

import java.math.BigDecimal;
import java.util.UUID;

public class BudgetExceededException extends RuntimeException {

    public BudgetExceededException(UUID userId, int year, int month, Category category,
            BigDecimal spent, BigDecimal limit) {
        super(String.format("Budget exceeded for userId=%s in %d/%d [%s]: spent=%s, limit=%s",
                userId, year, month,
                category == null ? "OVERALL" : category.name(),
                spent, limit));
    }
}
