CREATE SCHEMA IF NOT EXISTS carts;

CREATE TABLE carts.carts (
    id         BIGSERIAL    PRIMARY KEY,
    user_id    BIGINT       NOT NULL UNIQUE,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE carts.cart_items (
    id         BIGSERIAL  PRIMARY KEY,
    cart_id    BIGINT     NOT NULL REFERENCES carts.carts(id) ON DELETE CASCADE,
    product_id BIGINT     NOT NULL,
    quantity   INT        NOT NULL CHECK (quantity > 0)
);
