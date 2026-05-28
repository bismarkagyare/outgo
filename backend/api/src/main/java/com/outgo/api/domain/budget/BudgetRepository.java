package com.outgo.api.domain.budget;

import java.util.Optional;
import java.util.UUID;

public interface BudgetRepository {

    Budget save(Budget budget);

    Optional<Budget> findById(BudgetId id, UUID userId);

    void delete(BudgetId id, UUID userId);
}
