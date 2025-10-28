-- Seed data for Product table (Test environment)
-- This creates sample products with various configurations

INSERT INTO product (title, description, price, currency_id, unit_id, category_id, is_fresh, is_available) VALUES
('Organic Tomatoes', 'Fresh organic tomatoes from local farms', 2.99, 2, 1, 1, TRUE, TRUE),
('Whole Grain Bread', 'Artisan whole grain bread, baked daily', 3.50, 2, 2, 1, TRUE, TRUE),
('Free Range Eggs', 'Farm fresh free range eggs, pack of 12', 4.99, 1, 2, 1, TRUE, TRUE),
('Raw Honey', 'Pure organic raw honey, 500g jar', 8.99, 1, 1, 1, FALSE, TRUE),
('Almond Milk', 'Organic unsweetened almond milk, 1kg bottle', 3.29, 2, 1, 1, TRUE, TRUE),
('Quinoa', 'Organic white quinoa, premium quality', 5.99, 1, 1, 1, FALSE, TRUE),
('Fresh Salmon Fillet', 'Wild caught salmon fillet', 12.99, 1, 1, 1, TRUE, TRUE),
('Olive Oil', 'Extra virgin olive oil, cold pressed, 1kg bottle', 9.99, 2, 1, 1, FALSE, TRUE),
('Greek Yogurt', 'Organic Greek yogurt, 500g', 4.50, 2, 2, 1, TRUE, FALSE),
('Avocados', 'Fresh Hass avocados, ready to eat', 1.99, 1, 2, 1, TRUE, TRUE);

-- Link some products with certifications
-- Product 1 (Organic Tomatoes) -> Organic certification
INSERT INTO product_certification_link (product_id, certification_id) VALUES (1, 1);

-- Product 3 (Free Range Eggs) -> Organic certification
INSERT INTO product_certification_link (product_id, certification_id) VALUES (3, 1);

-- Product 4 (Raw Honey) -> Organic certification
INSERT INTO product_certification_link (product_id, certification_id) VALUES (4, 1);

-- Product 5 (Almond Milk) -> Organic certification
INSERT INTO product_certification_link (product_id, certification_id) VALUES (5, 1);

-- Product 6 (Quinoa) -> Organic certification
INSERT INTO product_certification_link (product_id, certification_id) VALUES (6, 1);

-- Product 8 (Olive Oil) -> Organic certification
INSERT INTO product_certification_link (product_id, certification_id) VALUES (8, 1);

-- Product 10 (Avocados) -> Organic certification
INSERT INTO product_certification_link (product_id, certification_id) VALUES (10, 1);

COMMENT ON TABLE product IS 'Test seed data for products with various price points and characteristics';
