CREATE SCHEMA IF NOT EXISTS users;

CREATE TABLE IF NOT EXISTS users.users (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role        VARCHAR(50)  NOT NULL DEFAULT 'CUSTOMER',
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

INSERT INTO users.users (name, email, password_hash, role)
VALUES ('Admin', 'admin@hometown.local', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5h8e0sN6cZ2bqkF1Yp0vqkN9bC7Hy', 'ADMIN')
ON CONFLICT (email) DO NOTHING;
