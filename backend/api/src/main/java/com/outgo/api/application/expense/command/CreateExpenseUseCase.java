package com.outgo.api.application.expense.command;

import com.outgo.api.domain.expense.Expense;
import com.outgo.api.domain.expense.ExpenseId;
import com.outgo.api.domain.expense.ExpenseRepository;
import com.outgo.api.domain.shared.DomainEvent;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CreateExpenseUseCase {

    private final ExpenseRepository expenseRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CreateExpenseUseCase(ExpenseRepository expenseRepository,
            ApplicationEventPublisher eventPublisher) {
        this.expenseRepository = expenseRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ExpenseId execute(CreateExpenseCommand command) {
        Expense expense = Expense.create(
                command.userId(),
                command.amount(),
                command.category(),
                command.description(),
                command.expenseDate());

        Expense saved = expenseRepository.save(expense);

        List<DomainEvent> events = saved.pullDomainEvents();
        events.forEach(eventPublisher::publishEvent);

        return saved.getId();
    }
}
