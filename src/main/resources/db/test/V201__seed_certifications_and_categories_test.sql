-- Test mock data for certifications and categories (minimal for testing)
INSERT INTO product_certification (label) VALUES
    ('Organic'),
    ('Vegan')
ON CONFLICT (label) DO NOTHING;

INSERT INTO product_category (label, slug) VALUES
    ('Fruits', 'fruits'),
    ('Vegetables', 'vegetables')
ON CONFLICT (label) DO NOTHING;
