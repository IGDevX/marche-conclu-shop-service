-- Create units table
CREATE TABLE IF NOT EXISTS units (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    label VARCHAR(255) NOT NULL
);

-- Create index on code for faster lookups
CREATE INDEX IF NOT EXISTS idx_units_code ON units(code);
