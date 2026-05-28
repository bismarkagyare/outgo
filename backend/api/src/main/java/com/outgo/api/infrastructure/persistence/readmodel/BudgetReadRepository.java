package com.outgo.api.infrastructure.persistence.readmodel;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class BudgetReadRepository {

    private final JdbcClient jdbcClient;

    public BudgetReadRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<BudgetSummaryDto> findByUserAndMonth(UUID userId, int year, int month) {
        return jdbcClient.sql("""
                SELECT id, amount, currency, category, year, month, created_at, updated_at
                FROM budgets
                WHERE user_id = :userId
                  AND year    = :year
                  AND month   = :month
                ORDER BY category NULLS FIRST
                """)
                .param("userId", userId)
                .param("year", year)
                .param("month", month)
                .query((rs, rowNum) -> new BudgetSummaryDto(
                        rs.getObject("id", UUID.class),
                        rs.getBigDecimal("amount"),
                        rs.getString("currency"),
                        rs.getString("category"),
                        rs.getInt("year"),
                        rs.getInt("month"),
                        rs.getTimestamp("created_at").toInstant(),
                        rs.getTimestamp("updated_at").toInstant()))
                .list();
    }

    public Optional<BudgetSummaryDto> findOverallBudgetByUserAndMonth(UUID userId, int year, int month) {
        return jdbcClient.sql("""
                SELECT id, amount, currency, category, year, month, created_at, updated_at
                FROM budgets
                WHERE user_id  = :userId
                  AND year     = :year
                  AND month    = :month
                  AND category IS NULL
                """)
                .param("userId", userId)
                .param("year", year)
                .param("month", month)
                .query((rs, rowNum) -> new BudgetSummaryDto(
                        rs.getObject("id", UUID.class),
                        rs.getBigDecimal("amount"),
                        rs.getString("currency"),
                        rs.getString("category"),
                        rs.getInt("year"),
                        rs.getInt("month"),
                        rs.getTimestamp("created_at").toInstant(),
                        rs.getTimestamp("updated_at").toInstant()))
                .optional();
    }

    public Optional<BudgetSummaryDto> findCategoryBudgetByUserAndMonth(UUID userId, int year, int month,
            String category) {
        return jdbcClient.sql("""
                SELECT id, amount, currency, category, year, month, created_at, updated_at
                FROM budgets
                WHERE user_id  = :userId
                  AND year     = :year
                  AND month    = :month
                  AND category = :category
                """)
                .param("userId", userId)
                .param("year", year)
                .param("month", month)
                .param("category", category)
                .query((rs, rowNum) -> new BudgetSummaryDto(
                        rs.getObject("id", UUID.class),
                        rs.getBigDecimal("amount"),
                        rs.getString("currency"),
                        rs.getString("category"),
                        rs.getInt("year"),
                        rs.getInt("month"),
                        rs.getTimestamp("created_at").toInstant(),
                        rs.getTimestamp("updated_at").toInstant()))
                .optional();
    }
}
