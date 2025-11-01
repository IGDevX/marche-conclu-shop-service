-- Create product table
CREATE TABLE IF NOT EXISTS product (
    id BIGSERIAL PRIMARY KEY,
    
    -- Basic information
    title VARCHAR(200) NOT NULL,
    description TEXT,
    
    -- Price and unit
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    currency_id BIGINT NOT NULL,
    unit_id BIGINT NOT NULL,
    
    -- Shelf (mandatory)
    shelf_id BIGINT NOT NULL,
    
    -- Image storage (cloud service)
    image_url VARCHAR(500),
    image_key VARCHAR(255),
    image_thumbnail_url VARCHAR(500),
    
    -- Product states
    is_fresh BOOLEAN NOT NULL DEFAULT FALSE,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    
    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    
    -- Foreign keys
    CONSTRAINT fk_product_currency FOREIGN KEY (currency_id) REFERENCES currency(id),
    CONSTRAINT fk_product_unit FOREIGN KEY (unit_id) REFERENCES units(id),
    CONSTRAINT fk_product_shelf FOREIGN KEY (shelf_id) REFERENCES shelf(id)
);

-- Create product_certification_link table (Many-to-Many)
CREATE TABLE IF NOT EXISTS product_certification_link (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    certification_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_pcl_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    CONSTRAINT fk_pcl_certification FOREIGN KEY (certification_id) REFERENCES product_certification(id) ON DELETE CASCADE,
    CONSTRAINT unique_product_certification UNIQUE (product_id, certification_id)
);

-- Indexes for performance
CREATE INDEX idx_product_shelf ON product(shelf_id) WHERE is_deleted = FALSE;
CREATE INDEX idx_product_is_deleted ON product(is_deleted);
CREATE INDEX idx_product_is_available ON product(is_available) WHERE is_deleted = FALSE;
CREATE INDEX idx_product_is_fresh ON product(is_fresh) WHERE is_deleted = FALSE;
CREATE INDEX idx_product_currency ON product(currency_id);
CREATE INDEX idx_product_unit ON product(unit_id);

CREATE INDEX idx_pcl_product ON product_certification_link(product_id);
CREATE INDEX idx_pcl_certification ON product_certification_link(certification_id);

-- Trigger to automatically update updated_at
CREATE OR REPLACE FUNCTION update_product_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER product_updated_at_trigger
    BEFORE UPDATE ON product
    FOR EACH ROW
    EXECUTE FUNCTION update_product_updated_at();

-- Comments
COMMENT ON TABLE product IS 'Stores product information with pricing, categorization, and image references';
COMMENT ON COLUMN product.title IS 'Product name (max 200 characters)';
COMMENT ON COLUMN product.description IS 'Detailed product description (max 2000 characters recommended)';
COMMENT ON COLUMN product.price IS 'Product price (must be positive)';
COMMENT ON COLUMN product.image_key IS 'Unique key for cloud storage (S3/MinIO)';
COMMENT ON COLUMN product.is_fresh IS 'Indicates if product is fresh/perishable';
COMMENT ON COLUMN product.is_available IS 'Indicates if product is currently available for sale';

COMMENT ON TABLE product_certification_link IS 'Many-to-Many relationship between products and certifications';
