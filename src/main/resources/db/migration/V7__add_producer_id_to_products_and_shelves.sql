-- Add producer_id column to product table
ALTER TABLE product
ADD COLUMN producer_id BIGINT;

-- Add producer_id column to shelf table
ALTER TABLE shelf
ADD COLUMN producer_id BIGINT;

-- Add NOT NULL constraint after updating existing data
-- In production, you would first populate these columns with valid producer IDs
-- For now, we'll set a default value for existing records
UPDATE product SET producer_id = 1 WHERE producer_id IS NULL;
UPDATE shelf SET producer_id = 1 WHERE producer_id IS NULL;

-- Now add the NOT NULL constraint
ALTER TABLE product
ALTER COLUMN producer_id SET NOT NULL;

ALTER TABLE shelf
ALTER COLUMN producer_id SET NOT NULL;

-- Add indexes for better query performance
CREATE INDEX idx_product_producer_id ON product(producer_id);
CREATE INDEX idx_shelf_producer_id ON shelf(producer_id);

-- Add composite indexes for common query patterns
CREATE INDEX idx_product_producer_deleted ON product(producer_id, is_deleted);
CREATE INDEX idx_product_producer_available ON product(producer_id, is_available, is_deleted);
CREATE INDEX idx_shelf_producer_deleted ON shelf(producer_id, is_deleted);

