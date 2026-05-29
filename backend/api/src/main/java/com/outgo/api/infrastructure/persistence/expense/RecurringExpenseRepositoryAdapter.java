package com.outgo.api.infrastructure.persistence.expense;

import com.outgo.api.domain.expense.Category;
import com.outgo.api.domain.expense.Frequency;
import com.outgo.api.domain.expense.RecurringExpense;
import com.outgo.api.domain.expense.RecurringExpenseId;
import com.outgo.api.domain.expense.RecurringExpenseRepository;
import com.outgo.api.domain.shared.Money;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RecurringExpenseRepositoryAdapter implements RecurringExpenseRepository {

    private final RecurringExpenseJpaRepository jpaRepository;

    public RecurringExpenseRepositoryAdapter(RecurringExpenseJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public RecurringExpense save(RecurringExpense recurringExpense) {
        RecurringExpenseJpaEntity entity = toEntity(recurringExpense);
        RecurringExpenseJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<RecurringExpense> findById(RecurringExpenseId id, UUID userId) {
        return jpaRepository.findByIdAndUserId(id.value(), userId).map(this::toDomain);
    }

    @Override
    public List<RecurringExpense> findAllDueAndActive(LocalDate asOf) {
        return jpaRepository.findAllDueAndActive(asOf).stream().map(this::toDomain).toList();
    }

    @Override
    public void delete(RecurringExpenseId id, UUID userId) {
        jpaRepository.deleteByIdAndUserId(id.value(), userId);
    }

    private RecurringExpenseJpaEntity toEntity(RecurringExpense re) {
        return new RecurringExpenseJpaEntity(
                re.getId().value(),
                re.getUserId(),
                re.getAmount().getAmount(),
                re.getAmount().getCurrency(),
                re.getCategory(),
                re.getDescription(),
                re.getFrequency(),
                re.getNextRunDate(),
                re.isActive(),
                re.getCreatedAt(),
                re.getUpdatedAt());
    }

    private RecurringExpense toDomain(RecurringExpenseJpaEntity e) {
        return RecurringExpense.reconstitute(
                RecurringExpenseId.of(e.getId()),
                e.getUserId(),
                Money.of(e.getAmount(), e.getCurrency()),
                e.getCategory(),
                e.getDescription(),
                e.getFrequency(),
                e.getNextRunDate(),
                e.isActive(),
                e.getCreatedAt(),
                e.getUpdatedAt());
    }
}
