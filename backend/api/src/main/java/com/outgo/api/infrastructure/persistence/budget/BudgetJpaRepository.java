package com.outgo.api.infrastructure.persistence.budget;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetJpaRepository extends JpaRepository<BudgetJpaEntity, UUID> {

    Optional<BudgetJpaEntity> findByIdAndUserId(UUID id, UUID userId);

    List<BudgetJpaEntity> findByUserIdAndYearAndMonth(UUID userId, int year, int month);

    void deleteByIdAndUserId(UUID id, UUID userId);
}
