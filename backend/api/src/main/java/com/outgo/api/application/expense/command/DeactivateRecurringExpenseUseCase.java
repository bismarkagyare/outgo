package com.outgo.api.application.expense.command;

import com.outgo.api.domain.expense.RecurringExpense;
import com.outgo.api.domain.expense.RecurringExpenseId;
import com.outgo.api.domain.expense.RecurringExpenseNotFoundException;
import com.outgo.api.domain.expense.RecurringExpenseRepository;
import com.outgo.api.domain.shared.DomainEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class DeactivateRecurringExpenseUseCase {

    private final RecurringExpenseRepository recurringExpenseRepository;
    private final ApplicationEventPublisher eventPublisher;

    public DeactivateRecurringExpenseUseCase(RecurringExpenseRepository recurringExpenseRepository,
            ApplicationEventPublisher eventPublisher) {
        this.recurringExpenseRepository = recurringExpenseRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void execute(RecurringExpenseId id, UUID userId) {
        RecurringExpense recurringExpense = recurringExpenseRepository.findById(id, userId)
                .orElseThrow(() -> new RecurringExpenseNotFoundException(id.value(), userId));

        recurringExpense.deactivate();
        recurringExpenseRepository.save(recurringExpense);

        List<DomainEvent> events = recurringExpense.pullDomainEvents();
        events.forEach(eventPublisher::publishEvent);
    }
}
