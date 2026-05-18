package com.outgo.api.domain.expense;

import java.util.Optional;
import java.util.UUID; 


public interface ExpenseRepository {
    Expense save(Expense expense);

    Optional<Expense> findById(ExpenseId id, UUID userId);
}
