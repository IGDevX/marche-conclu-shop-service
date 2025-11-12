-- Remove is_available column from product table (dev-local)
ALTER TABLE product DROP COLUMN IF EXISTS is_available;

