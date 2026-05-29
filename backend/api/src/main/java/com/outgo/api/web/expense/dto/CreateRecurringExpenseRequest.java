package com.outgo.api.web.expense.dto;

import com.outgo.api.domain.expense.Category;
import com.outgo.api.domain.expense.Frequency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateRecurringExpenseRequest(
        @NotNull @Positive BigDecimal amount,
        @NotBlank String currency,
        @NotNull Category category,
        String description,
        @NotNull Frequency frequency,
        @NotNull LocalDate startDate) {
}
