package com.outgo.api.application.expense.query;

import com.outgo.api.infrastructure.persistence.readmodel.ExpenseReadRepository;
import com.outgo.api.infrastructure.persistence.readmodel.ExpenseSummaryDto;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GetMonthlyExpensesQuery {

    private final ExpenseReadRepository readRepository;

    public GetMonthlyExpensesQuery(ExpenseReadRepository readRepository) {
        this.readRepository = readRepository;
    }

    @Transactional(readOnly = true)
    public List<ExpenseSummaryDto> execute(UUID userId, int year, int month) {
        return readRepository.findMonthlyExpenses(userId, year, month);
    }
}
