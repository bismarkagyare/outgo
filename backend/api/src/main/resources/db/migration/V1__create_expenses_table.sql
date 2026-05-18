-- =============================================================================
-- V1__create_expenses_table.sql
--
-- Creates the core `expenses` table for Outgo.
--
-- DESIGN DECISIONS
-- ----------------
-- 1. UUID primary key (not BIGSERIAL / auto-increment).
--    WHY: UUIDs are generated in the Java domain layer (ExpenseId.generate()).
--         The database never needs to produce an ID. This keeps the domain
--         self-contained and makes IDs safe to generate offline / before insert.
--
-- 2. Monetary amount stored as NUMERIC(19, 2), NOT as FLOAT / REAL.
--    WHY: FLOAT and REAL use binary floating-point and cannot represent 0.10 exactly.
--         NUMERIC stores the exact decimal value — mandated for financial data.
--         19 significant digits gives headroom for every realistic personal expense.
--         The (2) scale locks us to cents, matching BigDecimal(scale=2) in Money.java.
--
-- 3. currency is VARCHAR(3) alongside amount.
--    WHY: Money is a value object with both a magnitude and a currency. Storing just
--         the number without the unit is meaningless. VARCHAR(3) fits ISO 4217 codes
--         (GHS, USD, EUR …).
--
-- 4. category is VARCHAR(50) + CHECK constraint, not a PostgreSQL ENUM type.
--    WHY: PostgreSQL ENUM types are hard to extend — adding a value requires
--         ALTER TYPE, which locks the table. A CHECK constraint can be dropped and
--         replaced in a new Flyway migration with no table lock on PostgreSQL 16.
--
-- 5. All timestamps are TIMESTAMPTZ (timestamp with time zone).
--    WHY: TIMESTAMPTZ stores UTC internally and adjusts to the session time zone on
--         read. Java's Instant maps directly to TIMESTAMPTZ without conversion bugs.
--         TIMESTAMP WITHOUT TIME ZONE is ambiguous and must never be used for events.
--
-- 6. Soft-delete via nullable deleted_at column.
--    WHY: Financial records must be preserved for audit and regulatory purposes.
--         deleted_at IS NULL  → expense is active.
--         deleted_at IS NOT NULL → expense is soft-deleted; visible only to admin queries.
--
-- 7. Indexes are chosen to cover the two main query access patterns:
--    a. "All active expenses for a user, ordered by date" — used by monthly summaries.
--    b. "All active expenses for a user in a given category" — used in Phase 8 budget checks.
--    A partial index (WHERE deleted_at IS NULL) is used where possible to keep the
--    index small and fast — deleted rows are never in hot query paths.
-- =============================================================================

-- -----------------------------------------------------------------------------
-- TABLE: expenses
-- -----------------------------------------------------------------------------
CREATE TABLE expenses (

    -- IDENTITY ----------------------------------------------------------------

    -- UUID provided by Java (ExpenseId.generate() → UUID.randomUUID()).
    -- PRIMARY KEY implicitly creates a unique B-tree index on this column.
    id          UUID            NOT NULL,

    -- The authenticated user who owns this expense.
    -- NOT NULL because every expense must have an owner.
    -- NOT a foreign key to a users table yet — that table is added in a later migration.
    -- The application layer always scopes every query to this column (security requirement).
    user_id     UUID            NOT NULL,

    -- MONETARY AMOUNT ---------------------------------------------------------

    -- The numeric magnitude of the expense, e.g. 25.50.
    -- NUMERIC(19, 2): 19 total significant digits, exactly 2 decimal places (cents).
    -- CHECK amount > 0: belt-and-suspenders — Money.of() already enforces this in Java,
    -- but the database constraint provides a second line of defence.
    amount      NUMERIC(19, 2)  NOT NULL,

    -- ISO 4217 currency code, e.g. 'GHS', 'USD', 'EUR'.
    -- VARCHAR(3): all ISO 4217 codes are exactly 3 characters.
    -- NOT NULL: an amount without a currency is meaningless.
    currency    VARCHAR(3)      NOT NULL,

    -- CLASSIFICATION ----------------------------------------------------------

    -- The expense category. Stored as the enum name (string) via @Enumerated(EnumType.STRING).
    -- VARCHAR(50) is wide enough for the longest current value ('ENTERTAINMENT' = 13 chars)
    -- with room to spare if a longer category is ever added.
    -- NOT NULL: every expense must be categorised.
    category    VARCHAR(50)     NOT NULL,

    -- DESCRIPTION -------------------------------------------------------------

    -- Optional freeform note about the expense (e.g. "team lunch at Papaye").
    -- TEXT: no length limit — users should never be forced to truncate their notes.
    -- NULL allowed: description is optional in the domain (Expense.create() accepts null).
    description TEXT            NULL,

    -- DATES -------------------------------------------------------------------

    -- When the expense occurred IN REAL LIFE (user-provided).
    -- This is NOT the same as created_at — a user can record a past expense backdated
    -- to last week. The application layer uses this for monthly grouping.
    -- TIMESTAMPTZ: stores UTC, no timezone ambiguity. Maps to Java's Instant.
    expense_date TIMESTAMPTZ    NOT NULL,

    -- AUDIT TIMESTAMPS --------------------------------------------------------

    -- When this row was first inserted. Set once, never updated.
    -- Maps to Expense.createdAt (Instant), which is set to Instant.now() in Expense.create().
    created_at  TIMESTAMPTZ     NOT NULL,

    -- When this row was last modified (any field change: update or soft-delete).
    -- Maps to Expense.updatedAt (Instant), updated on every Expense.update() and Expense.delete() call.
    updated_at  TIMESTAMPTZ     NOT NULL,

    -- Soft-delete marker. NULL = active expense. Non-null = soft-deleted.
    -- Maps to Expense.deletedAt (Instant). Set by Expense.delete(), which calls Instant.now().
    -- NEVER hard-delete financial records — see design decision 6 above.
    deleted_at  TIMESTAMPTZ     NULL,

    -- CONSTRAINTS -------------------------------------------------------------

    -- Primary key: uniquely identifies each expense row.
    CONSTRAINT pk_expenses PRIMARY KEY (id),

    -- Enforce the amount is strictly positive at the database level.
    -- The domain (Money.of) already validates this, but defence-in-depth matters for finance.
    CONSTRAINT chk_expenses_amount_positive CHECK (amount > 0),

    -- Enforce only known category values can be stored.
    -- This list MUST stay in sync with the Category enum in category.java.
    -- If a new category is added to the enum, add a new Flyway migration that
    -- drops this constraint and adds a replacement — DO NOT edit this file.
    CONSTRAINT chk_expenses_category CHECK (
        category IN (
            'FOOD',
            'TRANSPORT',
            'HOUSING',
            'ENTERTAINMENT',
            'HEALTH',
            'EDUCATION',
            'SHOPPING',
            'SUBSCRIPTION',
            'UTILITIES',
            'TRAVEL',
            'OTHER'
        )
    ),

    -- Enforce currency is a non-empty 3-character uppercase code.
    -- This guards against saving an empty string or a too-long value.
    CONSTRAINT chk_expenses_currency CHECK (
        char_length(currency) = 3
        AND currency = upper(currency)  -- must already be upper-cased (Money.of() enforces this in Java)
    )
);

-- =============================================================================
-- INDEXES
-- =============================================================================

-- INDEX 1: Base filter — every query starts with user_id.
-- Even when we add category or date filters, Postgres uses this index first for the
-- user_id equality predicate before applying the secondary filter.
-- Partial (WHERE deleted_at IS NULL): active records are the hot path; deleted rows
-- are almost never queried directly and should not pollute the index.
CREATE INDEX idx_expenses_user_id
    ON expenses (user_id)
    WHERE deleted_at IS NULL;
-- WHY partial?: The index stays small because it only tracks active rows.
-- When Postgres sees "WHERE user_id = ? AND deleted_at IS NULL" it hits this index directly
-- without scanning tombstoned rows.

-- INDEX 2: Monthly expense query — "show me all active expenses for user X in month Y".
-- This is the most frequent read pattern in the application.
-- expense_date DESC: default sort is newest-first, so the index is already in the right order.
CREATE INDEX idx_expenses_user_date
    ON expenses (user_id, expense_date DESC)
    WHERE deleted_at IS NULL;
-- WHY compound?: Postgres can satisfy "user_id = ? AND expense_date BETWEEN ... AND ..."
-- entirely from this index without touching the table for the filter columns.

-- INDEX 3: Category budget check — "what is the total active spend for user X in category Y?"
-- Used in Phase 8 when the budget aggregate listens to ExpenseCreatedEvent and sums spending.
CREATE INDEX idx_expenses_user_category
    ON expenses (user_id, category)
    WHERE deleted_at IS NULL;
-- WHY include category?: The budget threshold query groups by (user_id, category).
-- With this index Postgres can answer that query using an index-only scan.
