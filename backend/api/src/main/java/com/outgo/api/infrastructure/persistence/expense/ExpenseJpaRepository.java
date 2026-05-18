package com.outgo.api.infrastructure.persistence.expense;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ExpenseJpaRepository extends JpaRepository<ExpenseJpaEntity, UUID> {

    @Query("""
            SELECT e FROM ExpenseJpaEntity e
            WHERE e.id = :id
              AND e.userId = :userId
              AND e.deletedAt IS NULL
            """)
    Optional<ExpenseJpaEntity> findByIdAndUserIdAndNotDeleted(
            @Param("id") UUID id,
            @Param("userId") UUID userId);
}
