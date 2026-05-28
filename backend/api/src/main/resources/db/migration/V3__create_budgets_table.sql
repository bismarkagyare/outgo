CREATE TABLE budgets (
    id         UUID          NOT NULL,
    user_id    UUID          NOT NULL,
    amount     NUMERIC(19,2) NOT NULL,
    currency   VARCHAR(3)    NOT NULL,
    category   VARCHAR(50)   NULL,
    year       INT           NOT NULL,
    month      INT           NOT NULL,
    created_at TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_budgets           PRIMARY KEY (id),
    CONSTRAINT uq_budget_per_month  UNIQUE (user_id, year, month, category),
    CONSTRAINT chk_budget_amount    CHECK (amount > 0),
    CONSTRAINT chk_budget_month     CHECK (month BETWEEN 1 AND 12),
    CONSTRAINT chk_budget_year      CHECK (year BETWEEN 2000 AND 2100)
);

CREATE INDEX idx_budgets_user_year_month ON budgets (user_id, year, month);
