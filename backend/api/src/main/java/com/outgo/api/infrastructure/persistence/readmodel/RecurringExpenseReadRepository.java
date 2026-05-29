package com.outgo.api.infrastructure.persistence.readmodel;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RecurringExpenseReadRepository {

    private final JdbcClient jdbcClient;

    public RecurringExpenseReadRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<RecurringExpenseSummaryDto> findByUser(UUID userId) {
        return jdbcClient.sql("""
                SELECT id, amount, currency, category, description, frequency,
                       next_run_date, active, created_at, updated_at
                FROM recurring_expenses
                WHERE user_id = :userId
                ORDER BY created_at DESC
                """)
                .param("userId", userId)
                .query((rs, rowNum) -> new RecurringExpenseSummaryDto(
                        rs.getObject("id", UUID.class),
                        rs.getBigDecimal("amount"),
                        rs.getString("currency"),
                        rs.getString("category"),
                        rs.getString("description"),
                        rs.getString("frequency"),
                        rs.getObject("next_run_date", LocalDate.class),
                        rs.getBoolean("active"),
                        rs.getObject("created_at", Instant.class),
                        rs.getObject("updated_at", Instant.class)))
                .list();
    }

    public Optional<RecurringExpenseSummaryDto> findById(UUID id, UUID userId) {
        return jdbcClient.sql("""
                SELECT id, amount, currency, category, description, frequency,
                       next_run_date, active, created_at, updated_at
                FROM recurring_expenses
                WHERE id = :id AND user_id = :userId
                """)
                .param("id", id)
                .param("userId", userId)
                .query((rs, rowNum) -> new RecurringExpenseSummaryDto(
                        rs.getObject("id", UUID.class),
                        rs.getBigDecimal("amount"),
                        rs.getString("currency"),
                        rs.getString("category"),
                        rs.getString("description"),
                        rs.getString("frequency"),
                        rs.getObject("next_run_date", LocalDate.class),
                        rs.getBoolean("active"),
                        rs.getObject("created_at", Instant.class),
                        rs.getObject("updated_at", Instant.class)))
                .optional();
    }
}
