package com.outgo.api.application.expense.command;

import com.outgo.api.domain.expense.RecurringExpense;
import com.outgo.api.domain.expense.RecurringExpenseId;
import com.outgo.api.domain.expense.RecurringExpenseRepository;
import com.outgo.api.domain.shared.DomainEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CreateRecurringExpenseUseCase {

    private final RecurringExpenseRepository recurringExpenseRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CreateRecurringExpenseUseCase(RecurringExpenseRepository recurringExpenseRepository,
            ApplicationEventPublisher eventPublisher) {
        this.recurringExpenseRepository = recurringExpenseRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public RecurringExpenseId execute(CreateRecurringExpenseCommand command) {
        RecurringExpense recurringExpense = RecurringExpense.create(
                command.userId(),
                command.amount(),
                command.category(),
                command.description(),
                command.frequency(),
                command.startDate());

        RecurringExpense saved = recurringExpenseRepository.save(recurringExpense);

        List<DomainEvent> events = saved.pullDomainEvents();
        events.forEach(eventPublisher::publishEvent);

        return saved.getId();
    }
}
