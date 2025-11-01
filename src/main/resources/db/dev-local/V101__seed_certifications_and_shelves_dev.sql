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

-- Dev-local mock data for shelves
-- producer_id = 1 corresponds to a user ID from user-service
-- Make sure to create a user with ID 1 in user-service for dev
INSERT INTO shelf (label, producer_id) VALUES
    ('Fruits', 1),
    ('Vegetables', 1),
    ('Meats', 1),
    ('Dairy Products', 1),
    ('Beverages', 1),
    ('Bakery', 1)
ON CONFLICT (producer_id, label) DO NOTHING;
