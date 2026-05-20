package com.outgo.api.infrastructure.persistence.readmodel;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ExpenseReadRepository {

    private final JdbcClient jdbcClient;

    public ExpenseReadRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<ExpenseSummaryDto> findMonthlyExpenses(UUID userId, int year, int month) {
        return jdbcClient.sql("""
                SELECT id, amount, currency, category, description,
                       expense_date, created_at, updated_at
                FROM expenses
                WHERE user_id    = :userId
                  AND deleted_at IS NULL
                  AND EXTRACT(YEAR  FROM expense_date) = :year
                  AND EXTRACT(MONTH FROM expense_date) = :month
                ORDER BY expense_date DESC
                """)
                .param("userId", userId)
                .param("year", year)
                .param("month", month)
                .query((rs, rowNum) -> new ExpenseSummaryDto(
                        rs.getObject("id", UUID.class),
                        rs.getBigDecimal("amount"),
                        rs.getString("currency"),
                        rs.getString("category"),
                        rs.getString("description"),
                        rs.getTimestamp("expense_date").toInstant(),
                        rs.getTimestamp("created_at").toInstant(),
                        rs.getTimestamp("updated_at").toInstant()))
                .list();
    }

    public Optional<ExpenseSummaryDto> findById(UUID id, UUID userId) {
        return jdbcClient.sql("""
                SELECT id, amount, currency, category, description,
                       expense_date, created_at, updated_at
                FROM expenses
                WHERE id      = :id
                  AND user_id = :userId
                  AND deleted_at IS NULL
                """)
                .param("id", id)
                .param("userId", userId)
                .query((rs, rowNum) -> new ExpenseSummaryDto(
                        rs.getObject("id", UUID.class),
                        rs.getBigDecimal("amount"),
                        rs.getString("currency"),
                        rs.getString("category"),
                        rs.getString("description"),
                        rs.getTimestamp("expense_date").toInstant(),
                        rs.getTimestamp("created_at").toInstant(),
                        rs.getTimestamp("updated_at").toInstant()))
                .optional();
    }
}
