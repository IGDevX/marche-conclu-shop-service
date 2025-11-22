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
-- Shelves thématiques cohérentes pour un producteur local
INSERT INTO shelf (label, producer_id) VALUES
    ('Fruits de Saison', 1),
    ('Nos Légumes Phares', 1),
    ('Viandes & Volailles Fermières', 1),
    ('Produits Laitiers du Terroir', 1),
    ('Boulangerie Artisanale', 1),
    ('Épicerie Fine', 1)
ON CONFLICT (producer_id, label) DO NOTHING;
