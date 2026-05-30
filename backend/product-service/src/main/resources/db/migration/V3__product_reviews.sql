CREATE TABLE IF NOT EXISTS products.product_reviews (
    id          BIGSERIAL PRIMARY KEY,
    product_id  BIGINT NOT NULL,
    user_id     BIGINT NOT NULL,
    reviewer_name VARCHAR(255),
    rating      INT NOT NULL,
    comment     TEXT,
    created_at  TIMESTAMPTZ DEFAULT now(),
    UNIQUE (product_id, user_id)
);
