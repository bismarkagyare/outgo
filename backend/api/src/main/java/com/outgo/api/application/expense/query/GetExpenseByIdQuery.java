package com.outgo.api.application.expense.query;

import com.outgo.api.domain.expense.ExpenseId;
import com.outgo.api.domain.expense.ExpenseNotFoundException;
import com.outgo.api.infrastructure.persistence.readmodel.ExpenseReadRepository;
import com.outgo.api.infrastructure.persistence.readmodel.ExpenseSummaryDto;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GetExpenseByIdQuery {

    private final ExpenseReadRepository readRepository;

    public GetExpenseByIdQuery(ExpenseReadRepository readRepository) {
        this.readRepository = readRepository;
    }

    @Transactional(readOnly = true)
    public ExpenseSummaryDto execute(ExpenseId id, UUID userId) {
        return readRepository.findById(id.getValue(), userId)
                .orElseThrow(() -> new ExpenseNotFoundException(id, userId));
    }
}
