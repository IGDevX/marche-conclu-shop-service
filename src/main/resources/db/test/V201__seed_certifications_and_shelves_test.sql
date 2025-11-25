-- Test mock data for certifications and categories (minimal for testing)
INSERT INTO product_certification (label) VALUES
    ('Organic'),
    ('Vegan')
ON CONFLICT (label) WHERE (is_deleted = FALSE) DO NOTHING;

-- producer_id = 1 corresponds to a test user from user-service
INSERT INTO shelf (label, producer_id) VALUES
    ('Fruits', 1),
    ('Vegetables', 1)
ON CONFLICT (producer_id, label) WHERE (is_deleted = FALSE) DO NOTHING;
