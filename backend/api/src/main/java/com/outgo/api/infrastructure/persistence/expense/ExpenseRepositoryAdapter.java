package com.outgo.api.infrastructure.persistence.expense;

import com.outgo.api.domain.expense.Expense;
import com.outgo.api.domain.expense.ExpenseId;
import com.outgo.api.domain.expense.ExpenseRepository;
import com.outgo.api.domain.shared.Money;

import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class ExpenseRepositoryAdapter implements ExpenseRepository {

    private final ExpenseJpaRepository jpaRepository;

    public ExpenseRepositoryAdapter(ExpenseJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Expense save(Expense expense) {
        ExpenseJpaEntity entity = toEntity(expense);
        ExpenseJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Expense> findById(ExpenseId id, UUID userId) {
        return jpaRepository
                .findByIdAndUserIdAndNotDeleted(id.getValue(), userId)
                .map(this::toDomain);
    }

    private ExpenseJpaEntity toEntity(Expense expense) {
        return new ExpenseJpaEntity(
                expense.getId().getValue(),
                expense.getUserId(),
                expense.getAmount().getAmount(),
                expense.getAmount().getCurrency(),
                expense.getCategory(),
                expense.getDescription(),
                expense.getExpenseDate(),
                expense.getCreatedAt(),
                expense.getUpdatedAt(),
                expense.getDeletedAt());
    }

    private Expense toDomain(ExpenseJpaEntity entity) {
        return Expense.reconstitute(
                ExpenseId.of(entity.getId()),
                entity.getUserId(),
                Money.of(entity.getAmount(), entity.getCurrency()),
                entity.getCategory(),
                entity.getDescription(),
                entity.getExpenseDate(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt());
    }
}
