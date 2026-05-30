CREATE SCHEMA IF NOT EXISTS products;

CREATE TABLE products.categories (
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(255) NOT NULL UNIQUE,
    slug    VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE products.products (
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(255) NOT NULL,
    description      TEXT,
    price            NUMERIC(12, 2) NOT NULL,
    discount_percent INT          NOT NULL DEFAULT 0,
    category_id      BIGINT       NOT NULL REFERENCES products.categories(id),
    stock            INT          NOT NULL DEFAULT 0,
    active           BOOLEAN      NOT NULL DEFAULT TRUE,
    seller_id        BIGINT,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE products.product_images (
    product_id BIGINT       NOT NULL REFERENCES products.products(id) ON DELETE CASCADE,
    url        VARCHAR(1024) NOT NULL
);

CREATE INDEX idx_products_category ON products.products(category_id);
CREATE INDEX idx_products_name ON products.products(name);
CREATE INDEX idx_product_images_product ON products.product_images(product_id);

-- Seed categories
INSERT INTO products.categories (name, slug) VALUES
    ('Wall Art',   'wall-art'),
    ('Pottery',    'pottery'),
    ('Textiles',   'textiles'),
    ('Home Decor', 'home-decor');

-- Seed sample handmade products
INSERT INTO products.products (name, description, price, discount_percent, category_id, stock, active) VALUES
    ('Hand-Painted Mandala Canvas',
     'Intricate mandala design hand-painted on stretched cotton canvas. Each piece is one of a kind.',
     1299.00, 10,
     (SELECT id FROM products.categories WHERE slug = 'wall-art'),
     15, TRUE),

    ('Terracotta Planter Set',
     'Set of 3 hand-thrown terracotta planters with hand-etched geometric patterns. Ideal for succulents.',
     849.00, 0,
     (SELECT id FROM products.categories WHERE slug = 'pottery'),
     30, TRUE),

    ('Madhubani Table Runner',
     'Traditional Madhubani motifs hand-painted on natural cotton. 13 × 72 inches.',
     699.00, 5,
     (SELECT id FROM products.categories WHERE slug = 'textiles'),
     20, TRUE),

    ('Blue Pottery Serving Bowl',
     'Authentic Jaipur blue pottery serving bowl with floral inlay. Microwave safe.',
     1099.00, 15,
     (SELECT id FROM products.categories WHERE slug = 'pottery'),
     12, TRUE),

    ('Macramé Wall Hanging',
     'Bohemian macramé wall hanging in natural cotton rope, 24 × 36 inches.',
     549.00, 0,
     (SELECT id FROM products.categories WHERE slug = 'wall-art'),
     25, TRUE),

    ('Hand-Block-Printed Cushion Cover',
     'Set of 2 cushion covers in vegetable-dyed cotton with Bagru block prints. 16 × 16 inches.',
     449.00, 10,
     (SELECT id FROM products.categories WHERE slug = 'textiles'),
     40, TRUE);
