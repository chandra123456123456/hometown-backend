CREATE SCHEMA IF NOT EXISTS analytics;

CREATE TABLE analytics.visit_events (
    id          BIGSERIAL PRIMARY KEY,
    type        VARCHAR(50)  NOT NULL,
    product_id  BIGINT,
    seller_id   BIGINT,
    category    VARCHAR(255),
    user_id     BIGINT,
    guest       BOOLEAN      NOT NULL DEFAULT FALSE,
    session_id  VARCHAR(255) NOT NULL,
    referrer    VARCHAR(1024),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_visit_events_type       ON analytics.visit_events (type);
CREATE INDEX idx_visit_events_seller_id  ON analytics.visit_events (seller_id);
CREATE INDEX idx_visit_events_product_id ON analytics.visit_events (product_id);
