CREATE SCHEMA IF NOT EXISTS orders;

CREATE TABLE orders.orders (
    id                      BIGSERIAL PRIMARY KEY,
    user_id                 BIGINT        NOT NULL,
    status                  VARCHAR(20)   NOT NULL,
    total_amount            NUMERIC(19,2) NOT NULL,
    shipping_address        TEXT,
    dest_pincode            VARCHAR(20),
    shipping_partner        VARCHAR(100),
    shipping_cost           NUMERIC(19,2),
    estimated_delivery_days INTEGER,
    created_at              TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE TABLE orders.order_items (
    id          BIGSERIAL PRIMARY KEY,
    order_id    BIGINT        NOT NULL REFERENCES orders.orders(id),
    product_id  BIGINT        NOT NULL,
    quantity    INTEGER       NOT NULL,
    price       NUMERIC(19,2) NOT NULL,
    seller_id   BIGINT
);
