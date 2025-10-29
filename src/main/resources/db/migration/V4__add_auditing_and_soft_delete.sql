-- Add auditing and soft delete columns to units table
ALTER TABLE units
    ADD COLUMN created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

-- Add index on is_deleted for faster queries
CREATE INDEX IF NOT EXISTS idx_units_is_deleted ON units(is_deleted);

-- Add auditing and soft delete columns to product_certification table
ALTER TABLE product_certification
    ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

-- Add index on is_deleted for faster queries
CREATE INDEX IF NOT EXISTS idx_product_certification_is_deleted ON product_certification(is_deleted);

-- Rename deleted_at to is_deleted for product_category table for consistency
-- First, add the new column
ALTER TABLE product_category
    ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

-- Set is_deleted to true where deleted_at is not null
UPDATE product_category
SET is_deleted = TRUE
WHERE deleted_at IS NOT NULL;

-- Drop the old deleted_at column
ALTER TABLE product_category
    DROP COLUMN IF EXISTS deleted_at;

-- Add index on is_deleted for faster queries
CREATE INDEX IF NOT EXISTS idx_product_category_is_deleted ON product_category(is_deleted);

-- Create trigger function to automatically update updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create triggers for units table
DROP TRIGGER IF EXISTS update_units_updated_at ON units;
CREATE TRIGGER update_units_updated_at
    BEFORE UPDATE ON units
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Create triggers for product_certification table
DROP TRIGGER IF EXISTS update_product_certification_updated_at ON product_certification;
CREATE TRIGGER update_product_certification_updated_at
    BEFORE UPDATE ON product_certification
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Create triggers for product_category table
DROP TRIGGER IF EXISTS update_product_category_updated_at ON product_category;
CREATE TRIGGER update_product_category_updated_at
    BEFORE UPDATE ON product_category
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
