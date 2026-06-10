package com.outgo.api.infrastructure.metrics;

import com.outgo.api.domain.budget.BudgetCreatedEvent;
import com.outgo.api.domain.expense.ExpenseCreatedEvent;
import com.outgo.api.domain.expense.ExpenseDeletedEvent;
import com.outgo.api.domain.expense.ExpenseUpdatedEvent;
import com.outgo.api.domain.expense.RecurringExpenseCreatedEvent;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ExpenseMetricsEventListener {

    private final MeterRegistry meterRegistry;

    public ExpenseMetricsEventListener(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @EventListener
    public void onExpenseCreated(ExpenseCreatedEvent event) {
        meterRegistry.counter("expense.created",
                "category", event.category().name()).increment();
    }

    @EventListener
    public void onExpenseUpdated(ExpenseUpdatedEvent event) {
        meterRegistry.counter("expense.updated",
                "category", event.newCategory().name()).increment();
    }

    @EventListener
    public void onExpenseDeleted(ExpenseDeletedEvent event) {
        meterRegistry.counter("expense.deleted").increment();
    }

    @EventListener
    public void onRecurringExpenseCreated(RecurringExpenseCreatedEvent event) {
        meterRegistry.counter("recurring.expense.created",
                "frequency", event.frequency().name(),
                "category", event.category().name()).increment();
    }

    @EventListener
    public void onBudgetCreated(BudgetCreatedEvent event) {
        meterRegistry.counter("budget.created",
                "category", event.category() != null ? event.category().name() : "overall").increment();
    }
}
