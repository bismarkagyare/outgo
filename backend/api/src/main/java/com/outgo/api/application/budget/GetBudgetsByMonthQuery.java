package com.outgo.api.application.budget;

import com.outgo.api.infrastructure.persistence.readmodel.BudgetReadRepository;
import com.outgo.api.infrastructure.persistence.readmodel.BudgetSummaryDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GetBudgetsByMonthQuery {

    private final BudgetReadRepository readRepository;

    public GetBudgetsByMonthQuery(BudgetReadRepository readRepository) {
        this.readRepository = readRepository;
    }

    @Transactional(readOnly = true)
    public List<BudgetSummaryDto> execute(UUID userId, int year, int month) {
        return readRepository.findByUserAndMonth(userId, year, month);
    }
}
