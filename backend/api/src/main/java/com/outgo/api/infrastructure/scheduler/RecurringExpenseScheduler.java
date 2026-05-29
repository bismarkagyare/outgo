package com.outgo.api.infrastructure.scheduler;

import com.outgo.api.application.expense.command.ProcessRecurringExpenseUseCase;
import com.outgo.api.domain.expense.RecurringExpense;
import com.outgo.api.domain.expense.RecurringExpenseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Component
public class RecurringExpenseScheduler {

    private static final Logger log = LoggerFactory.getLogger(RecurringExpenseScheduler.class);

    private final RecurringExpenseRepository recurringExpenseRepository;
    private final ProcessRecurringExpenseUseCase processRecurringExpenseUseCase;

    public RecurringExpenseScheduler(RecurringExpenseRepository recurringExpenseRepository,
            ProcessRecurringExpenseUseCase processRecurringExpenseUseCase) {
        this.recurringExpenseRepository = recurringExpenseRepository;
        this.processRecurringExpenseUseCase = processRecurringExpenseUseCase;
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void processRecurringExpenses() {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        List<RecurringExpense> due = recurringExpenseRepository.findAllDueAndActive(today);
        log.info("Processing {} due recurring expenses for {}", due.size(), today);

        for (RecurringExpense recurring : due) {
            try {
                processRecurringExpenseUseCase.execute(recurring);
            } catch (Exception e) {
                log.error("Failed to process recurring expense id={}: {}",
                        recurring.getId().value(), e.getMessage());
            }
        }
    }
}
