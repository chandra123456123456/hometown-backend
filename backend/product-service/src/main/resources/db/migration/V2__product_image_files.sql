CREATE TABLE IF NOT EXISTS products.product_image_files (
    id           BIGSERIAL PRIMARY KEY,
    code         VARCHAR(64)  UNIQUE NOT NULL,
    content_type VARCHAR(100),
    file_name    VARCHAR(255),
    created_at   TIMESTAMPTZ  DEFAULT now()
);
