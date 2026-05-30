CREATE SCHEMA IF NOT EXISTS shipping;

CREATE TABLE shipping.shipments
(
    id           BIGSERIAL PRIMARY KEY,
    order_id     BIGINT         NOT NULL,
    partner      VARCHAR(64)    NOT NULL,
    status       VARCHAR(32)    NOT NULL,
    dest_pincode VARCHAR(16)    NOT NULL,
    charge       NUMERIC(12, 2) NOT NULL,
    eta_days     INTEGER        NOT NULL,
    created_at   TIMESTAMPTZ    NOT NULL DEFAULT now()
);

CREATE INDEX idx_shipments_order_id ON shipping.shipments (order_id);
