-- Create product_certification table
CREATE TABLE IF NOT EXISTS product_certification (
    id BIGSERIAL PRIMARY KEY,
    label VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Create shelf table
CREATE TABLE IF NOT EXISTS shelf (
    id BIGSERIAL PRIMARY KEY,
    label VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ NULL
);
