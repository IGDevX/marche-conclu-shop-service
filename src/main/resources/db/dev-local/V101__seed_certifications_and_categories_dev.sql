-- Dev-local mock data for certifications
INSERT INTO product_certification (label) VALUES
    ('Organic'),
    ('Fair Trade'),
    ('Gluten Free'),
    ('Vegan'),
    ('Non-GMO'),
    ('Halal'),
    ('Kosher')
ON CONFLICT (label) DO NOTHING;

-- Dev-local mock data for categories
INSERT INTO product_category (label, slug) VALUES
    ('Fruits', 'fruits'),
    ('Vegetables', 'vegetables'),
    ('Meats', 'meats'),
    ('Dairy Products', 'dairy-products'),
    ('Beverages', 'beverages'),
    ('Bakery', 'bakery')
ON CONFLICT (label) DO NOTHING;
