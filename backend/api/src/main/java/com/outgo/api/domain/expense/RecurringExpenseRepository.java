package com.outgo.api.domain.expense;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecurringExpenseRepository {
    RecurringExpense save(RecurringExpense recurringExpense);

    Optional<RecurringExpense> findById(RecurringExpenseId id, UUID userId);

    List<RecurringExpense> findAllDueAndActive(LocalDate asOf);

    void delete(RecurringExpenseId id, UUID userId);
}
