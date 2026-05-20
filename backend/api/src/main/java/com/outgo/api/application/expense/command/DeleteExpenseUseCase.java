package com.outgo.api.application.expense.command;

import com.outgo.api.domain.expense.Expense;
import com.outgo.api.domain.expense.ExpenseId;
import com.outgo.api.domain.expense.ExpenseNotFoundException;
import com.outgo.api.domain.expense.ExpenseRepository;
import com.outgo.api.domain.shared.DomainEvent;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class DeleteExpenseUseCase {

    private final ExpenseRepository expenseRepository;
    private final ApplicationEventPublisher eventPublisher;

    public DeleteExpenseUseCase(ExpenseRepository expenseRepository,
            ApplicationEventPublisher eventPublisher) {
        this.expenseRepository = expenseRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void execute(ExpenseId expenseId, UUID userId) {
        Expense expense = expenseRepository
                .findById(expenseId, userId)
                .orElseThrow(() -> new ExpenseNotFoundException(expenseId, userId));

        expense.delete();

        Expense saved = expenseRepository.save(expense);

        List<DomainEvent> events = saved.pullDomainEvents();
        events.forEach(eventPublisher::publishEvent);
    }
}
