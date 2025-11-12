-- Remove is_available column from product table as it's no longer used
ALTER TABLE product DROP COLUMN IF EXISTS is_available;

