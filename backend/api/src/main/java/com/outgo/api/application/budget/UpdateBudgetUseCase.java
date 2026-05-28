package com.outgo.api.application.budget;

import com.outgo.api.domain.budget.Budget;
import com.outgo.api.domain.budget.BudgetNotFoundException;
import com.outgo.api.domain.budget.BudgetRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateBudgetUseCase {

    private final BudgetRepository budgetRepository;
    private final ApplicationEventPublisher eventPublisher;

    public UpdateBudgetUseCase(BudgetRepository budgetRepository,
            ApplicationEventPublisher eventPublisher) {
        this.budgetRepository = budgetRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void execute(UpdateBudgetCommand command) {
        Budget budget = budgetRepository.findById(command.budgetId(), command.userId())
                .orElseThrow(() -> new BudgetNotFoundException(command.budgetId(), command.userId()));
        budget.update(command.newLimit());
        Budget saved = budgetRepository.save(budget);
        saved.pullDomainEvents().forEach(eventPublisher::publishEvent);
    }
}
