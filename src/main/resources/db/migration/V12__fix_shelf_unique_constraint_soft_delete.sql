-- Fix unique constraints to allow reusing values after soft delete
-- Using partial unique indexes instead of full constraints to allow unlimited soft deletes
-- Partial indexes only enforce uniqueness where is_deleted = FALSE

-- 1. Shelf: producer_id + label
ALTER TABLE shelf DROP CONSTRAINT IF EXISTS uq_shelf_producer_label;
DROP INDEX IF EXISTS uq_shelf_producer_label_not_deleted;
CREATE UNIQUE INDEX uq_shelf_producer_label_not_deleted
    ON shelf (producer_id, label)
    WHERE is_deleted = FALSE;
COMMENT ON INDEX uq_shelf_producer_label_not_deleted IS
    'Ensures uniqueness of producer_id + label for active shelves only (is_deleted = FALSE)';

-- 2. Unit: code
ALTER TABLE units DROP CONSTRAINT IF EXISTS units_code_key;
DROP INDEX IF EXISTS uq_unit_code_not_deleted;
CREATE UNIQUE INDEX uq_unit_code_not_deleted
    ON units (code)
    WHERE is_deleted = FALSE;
COMMENT ON INDEX uq_unit_code_not_deleted IS
    'Ensures uniqueness of code for active units only (is_deleted = FALSE)';

-- 3. Currency: code
ALTER TABLE currency DROP CONSTRAINT IF EXISTS currency_code_key;
DROP INDEX IF EXISTS uq_currency_code_not_deleted;
CREATE UNIQUE INDEX uq_currency_code_not_deleted
    ON currency (code)
    WHERE is_deleted = FALSE;
COMMENT ON INDEX uq_currency_code_not_deleted IS
    'Ensures uniqueness of code for active currencies only (is_deleted = FALSE)';

-- 4. Product Certification: label
ALTER TABLE product_certification DROP CONSTRAINT IF EXISTS product_certification_label_key;
DROP INDEX IF EXISTS uq_certification_label_not_deleted;
CREATE UNIQUE INDEX uq_certification_label_not_deleted
    ON product_certification (label)
    WHERE is_deleted = FALSE;
COMMENT ON INDEX uq_certification_label_not_deleted IS
    'Ensures uniqueness of label for active certifications only (is_deleted = FALSE)';

-- 5. Category: name
ALTER TABLE category DROP CONSTRAINT IF EXISTS category_name_key;
DROP INDEX IF EXISTS uq_category_name_not_deleted;
CREATE UNIQUE INDEX uq_category_name_not_deleted
    ON category (name)
    WHERE is_deleted = FALSE;
COMMENT ON INDEX uq_category_name_not_deleted IS
    'Ensures uniqueness of name for active categories only (is_deleted = FALSE)';

-- 6. Category: slug
ALTER TABLE category DROP CONSTRAINT IF EXISTS category_slug_key;
DROP INDEX IF EXISTS uq_category_slug_not_deleted;
CREATE UNIQUE INDEX uq_category_slug_not_deleted
    ON category (slug)
    WHERE is_deleted = FALSE;
COMMENT ON INDEX uq_category_slug_not_deleted IS
    'Ensures uniqueness of slug for active categories only (is_deleted = FALSE)';

