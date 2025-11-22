-- Seed data for Product table (Dev-Local environment)
-- Products organized by shelf for Producer 1
-- Shelf IDs: 1=Fruits de Saison, 2=Nos Légumes Phares, 3=Viandes & Volailles Fermières,
--            4=Produits Laitiers du Terroir, 5=Boulangerie Artisanale, 6=Épicerie Fine

INSERT INTO product (title, description, price, currency_id, unit_id, shelf_id, producer_id, is_fresh) VALUES

-- Shelf 1: Fruits de Saison
('Pommes Rouges Bio', 'Pommes rouges croquantes de notre verger, idéales pour croquer', 3.50, 2, 1, 1, 1, TRUE),
('Fraises de Plougastel', 'Fraises fraîches et sucrées, cultivées en Bretagne, barquette 250g', 4.99, 2, 2, 1, 1, TRUE),
('Poires Conférence', 'Poires juteuses et parfumées, cueillies à maturité', 3.20, 2, 1, 1, 1, TRUE),
('Framboises du Jardin', 'Framboises fraîchement cueillies, barquette 125g', 5.99, 2, 2, 1, 1, TRUE),
('Cerises Burlat', 'Cerises rouges et charnues de saison, parfaites en dessert', 7.50, 2, 1, 1, 1, TRUE),
('Abricots du Roussillon', 'Abricots gorgés de soleil, saveur intense', 4.20, 2, 1, 1, 1, TRUE),

-- Shelf 2: Nos Légumes Phares
('Tomates Anciennes Bio', 'Mélange de tomates anciennes multicolores, goût authentique', 5.50, 2, 1, 2, 1, TRUE),
('Carottes de Sable', 'Carottes tendres et sucrées cultivées en terre légère', 2.80, 2, 1, 2, 1, TRUE),
('Courgettes du Potager', 'Courgettes fraîches et fermes, idéales pour grillades', 3.20, 2, 1, 2, 1, TRUE),
('Épinards Frais', 'Jeunes pousses d''épinards, lavées et prêtes à cuisiner', 3.90, 2, 1, 2, 1, TRUE),
('Salades Mélangées', 'Mélange de salades croquantes du jardin, sachet 200g', 2.50, 2, 2, 2, 1, TRUE),
('Poireaux de Nos Champs', 'Poireaux tendres et parfumés, parfaits pour les potages', 3.80, 2, 1, 2, 1, TRUE),
('Basilic Frais', 'Basilic aromatique cultivé en pot, saveur méditerranéenne', 2.90, 2, 5, 2, 1, TRUE),

-- Shelf 3: Viandes & Volailles Fermières
('Poulet Fermier Label Rouge', 'Poulet fermier entier élevé en plein air, race traditionnelle', 12.90, 2, 1, 3, 1, TRUE),
('Filet de Bœuf Charolais', 'Filet de bœuf race Charolaise, viande persillée et tendre, 500g', 19.90, 2, 1, 3, 1, TRUE),
('Magret de Canard du Sud-Ouest', 'Magret de canard fermier du Gers, pièce environ 350g', 14.50, 2, 5, 3, 1, TRUE),
('Côtelettes d''Agneau Bio', 'Côtelettes d''agneau bio élevé au pré, lot de 4 pièces', 16.80, 2, 2, 3, 1, TRUE),

-- Shelf 4: Produits Laitiers du Terroir
('Œufs de Nos Poules', 'Œufs frais de poules élevées en plein air, boîte de 12', 4.50, 2, 7, 4, 1, TRUE),
('Lait Frais Entier', 'Lait entier de la ferme, non homogénéisé, bouteille 1L', 2.20, 2, 3, 4, 1, TRUE),
('Beurre Doux de Baratte', 'Beurre doux traditionnel fabriqué à la baratte, 250g', 4.80, 2, 2, 4, 1, TRUE),
('Yaourt Fermier Nature', 'Yaourts natures au lait entier, pot en verre consigné 125g', 1.50, 2, 5, 4, 1, TRUE),
('Fromage de Chèvre Frais', 'Fromage de chèvre frais crémeux, bûchette 150g', 5.90, 2, 2, 4, 1, TRUE),
('Comté 18 Mois', 'Comté affiné 18 mois AOP, fruité et fondant, portion 200g', 7.20, 2, 2, 4, 1, TRUE),

-- Shelf 5: Boulangerie Artisanale
('Pain de Campagne au Levain', 'Pain de campagne traditionnel au levain naturel, cuit au four à bois', 3.90, 2, 5, 5, 1, TRUE),
('Baguette Tradition', 'Baguette tradition française croustillante, Label Boulanger', 1.20, 2, 5, 5, 1, TRUE),
('Croissants Pur Beurre', 'Croissants artisanaux pur beurre AOP, sachet de 4', 5.50, 2, 2, 5, 1, TRUE),
('Pain aux Céréales', 'Pain complet aux 7 céréales, riche en fibres', 4.20, 2, 5, 5, 1, TRUE),
('Brioche Tressée', 'Brioche moelleuse tressée à l''ancienne, 400g', 5.80, 2, 2, 5, 1, TRUE),

-- Shelf 6: Épicerie Fine
('Miel de Lavande', 'Miel de lavande de Provence, récolte artisanale, pot 500g', 9.90, 2, 2, 6, 1, FALSE),
('Confiture de Fraises Maison', 'Confiture artisanale de fraises, 70% de fruits, pot 350g', 5.50, 2, 2, 6, 1, FALSE),
('Huile d''Olive Vierge Extra', 'Huile d''olive première pression à froid, bouteille 750ml', 12.50, 2, 3, 6, 1, FALSE),
('Pâtes Fraîches aux Œufs', 'Pâtes fraîches artisanales aux œufs de nos poules, 500g', 4.90, 2, 2, 6, 1, TRUE),
('Terrine de Campagne', 'Terrine de campagne maison aux herbes, bocal 180g', 6.80, 2, 2, 6, 1, TRUE),
('Rillettes de Canard', 'Rillettes de canard fermier du Sud-Ouest, bocal 200g', 7.90, 2, 2, 6, 1, FALSE);

-- Link products with certifications
-- Certification IDs: 1=Organic, 2=Fair Trade, 3=Gluten Free, 4=Vegan, 5=Non-GMO, 6=Halal, 7=Kosher

-- Organic products (Bio)
INSERT INTO product_certification_link (product_id, certification_id) VALUES
(1, 1),  -- Pommes Rouges Bio
(7, 1),  -- Tomates Anciennes Bio
(8, 1),  -- Carottes de Sable
(10, 1), -- Épinards Frais
(17, 1), -- Côtelettes d'Agneau Bio
(24, 1); -- Fromage de Chèvre Frais

-- Fair Trade products
INSERT INTO product_certification_link (product_id, certification_id) VALUES
(32, 2); -- Huile d'Olive Vierge Extra

-- Vegan products
INSERT INTO product_certification_link (product_id, certification_id) VALUES
(1, 4),  -- Pommes Rouges Bio
(2, 4),  -- Fraises de Plougastel
(3, 4),  -- Poires Conférence
(4, 4),  -- Framboises du Jardin
(5, 4),  -- Cerises Burlat
(6, 4),  -- Abricots du Roussillon
(7, 4),  -- Tomates Anciennes Bio
(8, 4),  -- Carottes de Sable
(9, 4),  -- Courgettes du Potager
(10, 4), -- Épinards Frais
(11, 4), -- Salades Mélangées
(12, 4), -- Poireaux de Nos Champs
(13, 4), -- Basilic Frais
(30, 4), -- Miel de Lavande
(31, 4), -- Confiture de Fraises Maison
(32, 4); -- Huile d'Olive Vierge Extra

-- Non-GMO products
INSERT INTO product_certification_link (product_id, certification_id) VALUES
(1, 5), (2, 5), (3, 5), (4, 5), (5, 5), (6, 5), -- Tous les fruits
(7, 5), (8, 5), (9, 5), (10, 5), (11, 5), (12, 5), (13, 5), -- Tous les légumes
(18, 5), (19, 5), (20, 5), (21, 5), (22, 5), (23, 5), (24, 5), (25, 5), -- Produits laitiers
(30, 5), (31, 5), (32, 5); -- Épicerie fine

COMMENT ON TABLE product IS 'Dev-local seed data - Products organized by thematic shelves for Producer 1';
