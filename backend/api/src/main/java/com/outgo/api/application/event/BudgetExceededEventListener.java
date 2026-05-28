package com.outgo.api.application.event;

import com.outgo.api.domain.budget.BudgetExceededException;
import com.outgo.api.domain.expense.ExpenseCreatedEvent;
import com.outgo.api.domain.expense.ExpenseUpdatedEvent;
import com.outgo.api.infrastructure.persistence.readmodel.BudgetReadRepository;
import com.outgo.api.infrastructure.persistence.readmodel.BudgetSummaryDto;
import com.outgo.api.infrastructure.persistence.readmodel.ExpenseReadRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.UUID;

@Component
public class BudgetExceededEventListener {

    private final BudgetReadRepository budgetReadRepository;
    private final ExpenseReadRepository expenseReadRepository;

    public BudgetExceededEventListener(BudgetReadRepository budgetReadRepository,
            ExpenseReadRepository expenseReadRepository) {
        this.budgetReadRepository = budgetReadRepository;
        this.expenseReadRepository = expenseReadRepository;
    }

    @EventListener
    public void onExpenseCreated(ExpenseCreatedEvent event) {
        int year = event.expenseDate().atZone(ZoneOffset.UTC).getYear();
        int month = event.expenseDate().atZone(ZoneOffset.UTC).getMonthValue();
        checkBudget(event.userId(), event.category().name(), event.amount().getCurrency(), year, month);
    }

    @EventListener
    public void onExpenseUpdated(ExpenseUpdatedEvent event) {
        int year = event.newExpenseDate().atZone(ZoneOffset.UTC).getYear();
        int month = event.newExpenseDate().atZone(ZoneOffset.UTC).getMonthValue();
        checkBudget(event.userId(), event.newCategory().name(), event.newAmount().getCurrency(), year, month);
    }

    private void checkBudget(UUID userId, String category, String currency, int year, int month) {
        budgetReadRepository.findOverallBudgetByUserAndMonth(userId, year, month)
                .filter(b -> b.currency().equals(currency))
                .ifPresent(b -> {
                    BigDecimal totalSpent = expenseReadRepository.sumByUserAndMonth(userId, year, month);
                    if (totalSpent.compareTo(b.amount()) > 0) {
                        throwExceeded(userId, year, month, null, totalSpent, b);
                    }
                });

        budgetReadRepository.findCategoryBudgetByUserAndMonth(userId, year, month, category)
                .filter(b -> b.currency().equals(currency))
                .ifPresent(b -> {
                    BigDecimal categorySpent = expenseReadRepository.sumByUserAndMonthAndCategory(
                            userId, year, month, category);
                    if (categorySpent.compareTo(b.amount()) > 0) {
                        throwExceeded(userId, year, month, category, categorySpent, b);
                    }
                });
    }

    private void throwExceeded(UUID userId, int year, int month, String category,
            BigDecimal spent, BudgetSummaryDto budget) {
        throw new BudgetExceededException(
                userId, year, month,
                category == null ? null
                        : com.outgo.api.domain.expense.Category.valueOf(category),
                spent, budget.amount());
    }
}
