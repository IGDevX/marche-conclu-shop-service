-- Create category table
CREATE TABLE category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    display_order INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- Add index on slug for faster lookups
CREATE INDEX idx_category_slug ON category(slug);
CREATE INDEX idx_category_is_deleted ON category(is_deleted);
CREATE INDEX idx_category_display_order ON category(display_order);

-- Add category_id to product table
ALTER TABLE product
ADD COLUMN category_id BIGINT;

-- Add foreign key constraint
ALTER TABLE product
ADD CONSTRAINT fk_product_category
FOREIGN KEY (category_id)
REFERENCES category(id)
ON DELETE RESTRICT;

-- Add index for better query performance
CREATE INDEX idx_product_category_id ON product(category_id);
CREATE INDEX idx_product_category_deleted ON product(category_id, is_deleted);

COMMENT ON TABLE category IS 'Global product categories shared across all producers for restaurant filtering';
COMMENT ON COLUMN category.name IS 'Category name (e.g., Fruits, Légumes)';
COMMENT ON COLUMN category.slug IS 'URL-friendly version of the name';
COMMENT ON COLUMN category.description IS 'Description with examples of products in this category';
COMMENT ON COLUMN category.display_order IS 'Display order for UI sorting';

