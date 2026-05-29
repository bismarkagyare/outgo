CREATE TABLE recurring_expenses (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    amount NUMERIC(19,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description VARCHAR(500),
    frequency VARCHAR(10) NOT NULL,
    next_run_date DATE NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT recurring_expenses_amount_positive CHECK (amount > 0),
    CONSTRAINT recurring_expenses_category_check CHECK (category IN (
        'FOOD', 'TRANSPORT', 'HOUSING', 'ENTERTAINMENT', 'HEALTH',
        'EDUCATION', 'SHOPPING', 'SUBSCRIPTION', 'UTILITIES', 'TRAVEL', 'OTHER'
    )),
    CONSTRAINT recurring_expenses_frequency_check CHECK (frequency IN (
        'DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY'
    ))
);

CREATE INDEX idx_recurring_expenses_user_id ON recurring_expenses(user_id);
CREATE INDEX idx_recurring_expenses_due ON recurring_expenses(next_run_date) WHERE active = true;
