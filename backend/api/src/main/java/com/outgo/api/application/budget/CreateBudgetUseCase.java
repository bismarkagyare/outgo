package com.outgo.api.application.budget;

import com.outgo.api.domain.budget.Budget;
import com.outgo.api.domain.budget.BudgetId;
import com.outgo.api.domain.budget.BudgetRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateBudgetUseCase {

    private final BudgetRepository budgetRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CreateBudgetUseCase(BudgetRepository budgetRepository,
            ApplicationEventPublisher eventPublisher) {
        this.budgetRepository = budgetRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public BudgetId execute(CreateBudgetCommand command) {
        Budget budget = Budget.create(
                command.userId(),
                command.limit(),
                command.category(),
                command.year(),
                command.month());
        Budget saved = budgetRepository.save(budget);
        saved.pullDomainEvents().forEach(eventPublisher::publishEvent);
        return saved.getId();
    }
}
