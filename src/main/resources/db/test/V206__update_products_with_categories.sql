-- Update product seeds with category_id for test environment

-- Update products with appropriate categories
-- Fruits category (id: 1)
UPDATE product SET category_id = 1 WHERE title = 'Avocados';

-- Légumes category (id: 2)
UPDATE product SET category_id = 2 WHERE title = 'Organic Tomatoes';

-- Poissons category (id: 4)
UPDATE product SET category_id = 4 WHERE title = 'Fresh Salmon Fillet';

-- Produits laitiers category (id: 6)
UPDATE product SET category_id = 6 WHERE title IN ('Free Range Eggs', 'Greek Yogurt');

-- Pain & Farines category (id: 8)
UPDATE product SET category_id = 8 WHERE title = 'Whole Grain Bread';

-- Épicerie category (id: 9)
UPDATE product SET category_id = 9 WHERE title IN ('Raw Honey', 'Almond Milk', 'Quinoa', 'Olive Oil');

-- Now make category_id NOT NULL
ALTER TABLE product ALTER COLUMN category_id SET NOT NULL;

COMMENT ON TABLE product IS 'Products updated with category_id for test environment';

