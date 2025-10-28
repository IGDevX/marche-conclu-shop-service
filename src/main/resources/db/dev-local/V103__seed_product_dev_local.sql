-- Seed data for Product table (Dev-Local environment)
-- This creates a comprehensive set of sample products

INSERT INTO product (title, description, price, currency_id, unit_id, category_id, is_fresh, is_available) VALUES
-- Fresh produce
('Organic Tomatoes', 'Fresh organic tomatoes from local farms, vine-ripened for optimal flavor', 2.99, 2, 1, 1, TRUE, TRUE),
('Fresh Spinach', 'Baby spinach leaves, pre-washed and ready to eat', 3.49, 2, 1, 1, TRUE, TRUE),
('Carrots', 'Organic carrots, crunchy and sweet', 1.99, 2, 1, 1, TRUE, TRUE),
('Red Apples', 'Crisp red apples, perfect for snacking', 3.99, 1, 1, 1, TRUE, TRUE),
('Bananas', 'Fresh bananas, rich in potassium', 1.49, 1, 1, 1, TRUE, TRUE),
('Avocados', 'Fresh Hass avocados, ready to eat', 1.99, 1, 2, 1, TRUE, TRUE),
('Strawberries', 'Sweet organic strawberries, 250g pack', 4.99, 2, 2, 1, TRUE, TRUE),
('Fresh Basil', 'Aromatic fresh basil, perfect for Italian cuisine', 2.49, 2, 2, 1, TRUE, TRUE),

-- Bakery
('Whole Grain Bread', 'Artisan whole grain bread, baked daily with organic flour', 3.50, 2, 2, 1, TRUE, TRUE),
('Sourdough Bread', 'Traditional sourdough bread with crispy crust', 4.99, 2, 2, 1, TRUE, TRUE),
('Croissants', 'Buttery French croissants, pack of 4', 5.99, 2, 2, 1, TRUE, TRUE),

-- Dairy
('Free Range Eggs', 'Farm fresh free range eggs, pack of 12', 4.99, 1, 2, 1, TRUE, TRUE),
('Organic Milk', 'Fresh organic whole milk, 1L', 2.99, 2, 3, 1, TRUE, TRUE),
('Greek Yogurt', 'Organic Greek yogurt, 500g', 4.50, 2, 2, 1, TRUE, TRUE),
('Cheddar Cheese', 'Aged cheddar cheese, 200g', 5.99, 1, 2, 1, TRUE, TRUE),

-- Proteins
('Fresh Salmon Fillet', 'Wild caught salmon fillet, rich in omega-3', 12.99, 1, 1, 1, TRUE, TRUE),
('Chicken Breast', 'Free range chicken breast, 500g', 7.99, 2, 1, 1, TRUE, TRUE),
('Ground Beef', 'Grass-fed ground beef, 500g', 8.99, 1, 1, 1, TRUE, TRUE),

-- Pantry staples
('Raw Honey', 'Pure organic raw honey, 500g jar', 8.99, 1, 1, 1, FALSE, TRUE),
('Almond Milk', 'Organic unsweetened almond milk, 1L', 3.29, 2, 3, 1, FALSE, TRUE),
('Quinoa', 'Organic white quinoa, premium quality, 500g', 5.99, 1, 1, 1, FALSE, TRUE),
('Olive Oil', 'Extra virgin olive oil, cold pressed, 750ml', 9.99, 2, 3, 1, FALSE, TRUE),
('Pasta', 'Organic whole wheat pasta, 500g', 2.99, 2, 2, 1, FALSE, TRUE),
('Brown Rice', 'Organic brown rice, 1kg', 4.49, 1, 1, 1, FALSE, TRUE),
('Peanut Butter', 'Natural peanut butter, no added sugar, 500g', 6.99, 1, 2, 1, FALSE, TRUE),

-- Unavailable product (for testing)
('Out of Stock Product', 'This product is currently unavailable', 9.99, 1, 2, 1, FALSE, FALSE);

-- Link products with certifications
-- Organic products
INSERT INTO product_certification_link (product_id, certification_id) VALUES 
(1, 1), (2, 1), (3, 1), (7, 1), (9, 1), (13, 1), (14, 1), (19, 1), (20, 1), (21, 1), (22, 1), (23, 1), (24, 1);

COMMENT ON TABLE product IS 'Dev-local seed data with comprehensive product catalog for testing';
