package com.outgo.api.infrastructure.persistence.expense;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecurringExpenseJpaRepository extends JpaRepository<RecurringExpenseJpaEntity, UUID> {

    Optional<RecurringExpenseJpaEntity> findByIdAndUserId(UUID id, UUID userId);

    @Query("SELECT r FROM RecurringExpenseJpaEntity r WHERE r.active = true AND r.nextRunDate <= :asOf")
    List<RecurringExpenseJpaEntity> findAllDueAndActive(@Param("asOf") LocalDate asOf);

    void deleteByIdAndUserId(UUID id, UUID userId);
}
