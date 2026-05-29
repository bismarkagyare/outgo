package com.outgo.api.application.expense.command;

import com.outgo.api.domain.expense.RecurringExpense;
import com.outgo.api.domain.expense.RecurringExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;

@Service
public class ProcessRecurringExpenseUseCase {

    private final RecurringExpenseRepository recurringExpenseRepository;
    private final CreateExpenseUseCase createExpenseUseCase;

    public ProcessRecurringExpenseUseCase(RecurringExpenseRepository recurringExpenseRepository,
            CreateExpenseUseCase createExpenseUseCase) {
        this.recurringExpenseRepository = recurringExpenseRepository;
        this.createExpenseUseCase = createExpenseUseCase;
    }

    @Transactional
    public void execute(RecurringExpense recurring) {
        CreateExpenseCommand command = new CreateExpenseCommand(
                recurring.getUserId(),
                recurring.getAmount(),
                recurring.getCategory(),
                recurring.getDescription(),
                recurring.getNextRunDate().atStartOfDay().toInstant(ZoneOffset.UTC));

        createExpenseUseCase.execute(command);

        recurring.advanceNextRunDate();
        recurringExpenseRepository.save(recurring);
    }
}
