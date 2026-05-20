package com.outgo.api.application.expense.command;

import com.outgo.api.domain.expense.Expense;
import com.outgo.api.domain.expense.ExpenseNotFoundException;
import com.outgo.api.domain.expense.ExpenseRepository;
import com.outgo.api.domain.shared.DomainEvent;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UpdateExpenseUseCase {

    private final ExpenseRepository expenseRepository;
    private final ApplicationEventPublisher eventPublisher;

    public UpdateExpenseUseCase(ExpenseRepository expenseRepository,
            ApplicationEventPublisher eventPublisher) {
        this.expenseRepository = expenseRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void execute(UpdateExpenseCommand command) {
        Expense expense = expenseRepository
                .findById(command.expenseId(), command.userId())
                .orElseThrow(() -> new ExpenseNotFoundException(command.expenseId(), command.userId()));

        expense.update(
                command.newAmount(),
                command.newCategory(),
                command.newDescription(),
                command.newExpenseDate());

        Expense saved = expenseRepository.save(expense);

        List<DomainEvent> events = saved.pullDomainEvents();
        events.forEach(eventPublisher::publishEvent);
    }
}
