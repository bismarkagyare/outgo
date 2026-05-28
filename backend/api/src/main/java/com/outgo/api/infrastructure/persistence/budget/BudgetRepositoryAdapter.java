package com.outgo.api.infrastructure.persistence.budget;

import com.outgo.api.domain.budget.Budget;
import com.outgo.api.domain.budget.BudgetId;
import com.outgo.api.domain.budget.BudgetRepository;
import com.outgo.api.domain.shared.Money;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class BudgetRepositoryAdapter implements BudgetRepository {

    private final BudgetJpaRepository budgetJpaRepository;

    public BudgetRepositoryAdapter(BudgetJpaRepository budgetJpaRepository) {
        this.budgetJpaRepository = budgetJpaRepository;
    }

    @Override
    public Budget save(Budget budget) {
        BudgetJpaEntity entity = toEntity(budget);
        BudgetJpaEntity saved = budgetJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Budget> findById(BudgetId id, UUID userId) {
        return budgetJpaRepository.findByIdAndUserId(id.getValue(), userId)
                .map(this::toDomain);
    }

    @Override
    public void delete(BudgetId id, UUID userId) {
        budgetJpaRepository.deleteByIdAndUserId(id.getValue(), userId);
    }

    private BudgetJpaEntity toEntity(Budget budget) {
        return new BudgetJpaEntity(
                budget.getId().getValue(),
                budget.getUserId(),
                budget.getLimit().getAmount(),
                budget.getLimit().getCurrency(),
                budget.getCategory(),
                budget.getYear(),
                budget.getMonth(),
                budget.getCreatedAt(),
                budget.getUpdatedAt());
    }

    private Budget toDomain(BudgetJpaEntity entity) {
        return Budget.reconstitute(
                BudgetId.of(entity.getId()),
                entity.getUserId(),
                Money.of(entity.getAmount(), entity.getCurrency()),
                entity.getCategory(),
                entity.getYear(),
                entity.getMonth(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
