CREATE SCHEMA IF NOT EXISTS payments;

CREATE TABLE payments.payments (
    id                  BIGSERIAL PRIMARY KEY,
    order_id            BIGINT        NOT NULL,
    amount              NUMERIC(19,2) NOT NULL,
    status              VARCHAR(10)   NOT NULL,
    mock_transaction_id VARCHAR(255),
    created_at          TIMESTAMPTZ   NOT NULL
);
