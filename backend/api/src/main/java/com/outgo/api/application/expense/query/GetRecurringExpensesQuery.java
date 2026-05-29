package com.outgo.api.application.expense.query;

import com.outgo.api.infrastructure.persistence.readmodel.RecurringExpenseReadRepository;
import com.outgo.api.infrastructure.persistence.readmodel.RecurringExpenseSummaryDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GetRecurringExpensesQuery {

    private final RecurringExpenseReadRepository readRepository;

    public GetRecurringExpensesQuery(RecurringExpenseReadRepository readRepository) {
        this.readRepository = readRepository;
    }

    @Transactional(readOnly = true)
    public List<RecurringExpenseSummaryDto> execute(UUID userId) {
        return readRepository.findByUser(userId);
    }
}
