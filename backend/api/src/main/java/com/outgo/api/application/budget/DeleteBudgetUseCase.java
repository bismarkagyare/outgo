package com.outgo.api.application.budget;

import com.outgo.api.domain.budget.BudgetId;
import com.outgo.api.domain.budget.BudgetNotFoundException;
import com.outgo.api.domain.budget.BudgetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DeleteBudgetUseCase {

    private final BudgetRepository budgetRepository;

    public DeleteBudgetUseCase(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    @Transactional
    public void execute(BudgetId budgetId, UUID userId) {
        budgetRepository.findById(budgetId, userId)
                .orElseThrow(() -> new BudgetNotFoundException(budgetId, userId));
        budgetRepository.delete(budgetId, userId);
    }
}
