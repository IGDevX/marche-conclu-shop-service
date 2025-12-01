-- Seed data for Product table (Dev-Local environment)
-- Products organized by shelf for Producer 1
-- Shelf IDs: 1=Fruits de Saison, 2=Nos Légumes Phares, 3=Viandes & Volailles Fermières,
--            4=Produits Laitiers du Terroir, 5=Boulangerie Artisanale, 6=Épicerie Fine
-- Category IDs: 1=Fruits, 2=Légumes, 3=Viandes, 6=Produits laitiers, 7=Œufs, 8=Pain & Farines, 9=Épicerie, 5=Charcuterie

INSERT INTO product (title, description, price, currency_id, unit_id, shelf_id, category_id, producer_id, is_fresh, main_image_url) VALUES

-- Shelf 1: Fruits de Saison (Category: Fruits = 1)
('Pommes Rouges Bio', 'Pommes rouges croquantes de notre verger, idéales pour croquer', 3.50, 2, 1, 1, 1, 1, TRUE, 'https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=800'),
('Fraises de Plougastel', 'Fraises fraîches et sucrées, cultivées en Bretagne, barquette 250g', 4.99, 2, 2, 1, 1, 1, TRUE, 'https://images.unsplash.com/photo-1464965911861-746a04b4bca6?w=800'),
('Poires Conférence', 'Poires juteuses et parfumées, cueillies à maturité', 3.20, 2, 1, 1, 1, 1, TRUE, 'https://images.unsplash.com/photo-1568702846914-96b305d2aaeb?w=800'),
('Framboises du Jardin', 'Framboises fraîchement cueillies, barquette 125g', 5.99, 2, 2, 1, 1, 1, TRUE, 'https://images.unsplash.com/photo-1577069861033-55d04cec4ef5?w=800'),
('Cerises Burlat', 'Cerises rouges et charnues de saison, parfaites en dessert', 7.50, 2, 1, 1, 1, 1, TRUE, 'https://images.unsplash.com/photo-1528821128474-27f963b062bf?w=800'),
('Abricots du Roussillon', 'Abricots gorgés de soleil, saveur intense', 4.20, 2, 1, 1, 1, 1, TRUE, 'https://images.unsplash.com/photo-1600271886742-f049cd451bba?w=800'),

-- Shelf 2: Nos Légumes Phares (Category: Légumes = 2)
('Tomates Anciennes Bio', 'Mélange de tomates anciennes multicolores, goût authentique', 5.50, 2, 1, 2, 2, 1, TRUE, 'https://images.unsplash.com/photo-1592841200221-a6898f307baa?w=800'),
('Carottes de Sable', 'Carottes tendres et sucrées cultivées en terre légère', 2.80, 2, 1, 2, 2, 1, TRUE, 'https://images.unsplash.com/photo-1598170845058-32b9d6a5da37?w=800'),
('Courgettes du Potager', 'Courgettes fraîches et fermes, idéales pour grillades', 3.20, 2, 1, 2, 2, 1, TRUE, 'https://images.unsplash.com/photo-1587735243615-c03f25aaff15?w=800'),
('Épinards Frais', 'Jeunes pousses d''épinards, lavées et prêtes à cuisiner', 3.90, 2, 1, 2, 2, 1, TRUE, 'https://images.unsplash.com/photo-1576045057995-568f588f82fb?w=800'),
('Salades Mélangées', 'Mélange de salades croquantes du jardin, sachet 200g', 2.50, 2, 2, 2, 2, 1, TRUE, 'https://images.unsplash.com/photo-1622206151226-18ca2c9ab4a1?w=800'),
('Poireaux de Nos Champs', 'Poireaux tendres et parfumés, parfaits pour les potages', 3.80, 2, 1, 2, 2, 1, TRUE, 'https://images.unsplash.com/photo-1626611297838-e2d3d6faebf4?w=800'),
('Basilic Frais', 'Basilic aromatique cultivé en pot, saveur méditerranéenne', 2.90, 2, 5, 2, 2, 1, TRUE, 'https://images.unsplash.com/photo-1618375569909-3c8616cf7733?w=800'),

-- Shelf 3: Viandes & Volailles Fermières (Category: Viandes = 3)
('Poulet Fermier Label Rouge', 'Poulet fermier entier élevé en plein air, race traditionnelle', 12.90, 2, 1, 3, 3, 1, TRUE, 'https://images.unsplash.com/photo-1587593810167-a84920ea0781?w=800'),
('Filet de Bœuf Charolais', 'Filet de bœuf race Charolaise, viande persillée et tendre, 500g', 19.90, 2, 1, 3, 3, 1, TRUE, 'https://images.unsplash.com/photo-1603048588665-791ca8aea617?w=800'),
('Magret de Canard du Sud-Ouest', 'Magret de canard fermier du Gers, pièce environ 350g', 14.50, 2, 5, 3, 3, 1, TRUE, 'https://images.unsplash.com/photo-1607623488235-6d7fa7829b0b?w=800'),
('Côtelettes d''Agneau Bio', 'Côtelettes d''agneau bio élevé au pré, lot de 4 pièces', 16.80, 2, 2, 3, 3, 1, TRUE, 'https://images.unsplash.com/photo-1602470520998-f4a52199a3d6?w=800'),

-- Shelf 4: Produits Laitiers du Terroir (Category: Œufs=7 for eggs, Produits laitiers=6 for dairy)
('Œufs de Nos Poules', 'Œufs frais de poules élevées en plein air, boîte de 12', 4.50, 2, 7, 4, 7, 1, TRUE, 'https://images.unsplash.com/photo-1582722872445-44dc5f7e3c8f?w=800'),
('Lait Frais Entier', 'Lait entier de la ferme, non homogénéisé, bouteille 1L', 2.20, 2, 3, 4, 6, 1, TRUE, 'https://images.unsplash.com/photo-1563636619-e9143da7973b?w=800'),
('Beurre Doux de Baratte', 'Beurre doux traditionnel fabriqué à la baratte, 250g', 4.80, 2, 2, 4, 6, 1, TRUE, 'https://images.unsplash.com/photo-1589985270826-4b7bb135bc9d?w=800'),
('Yaourt Fermier Nature', 'Yaourts natures au lait entier, pot en verre consigné 125g', 1.50, 2, 5, 4, 6, 1, TRUE, 'https://images.unsplash.com/photo-1488477181946-6428a0291777?w=800'),
('Fromage de Chèvre Frais', 'Fromage de chèvre frais crémeux, bûchette 150g', 5.90, 2, 2, 4, 6, 1, TRUE, 'https://images.unsplash.com/photo-1452195100486-9cc805987862?w=800'),
('Comté 18 Mois', 'Comté affiné 18 mois AOP, fruité et fondant, portion 200g', 7.20, 2, 2, 4, 6, 1, TRUE, 'https://images.unsplash.com/photo-1486297678162-eb2a19b0a32d?w=800'),

-- Shelf 5: Boulangerie Artisanale (Category: Pain & Farines = 8)
('Pain de Campagne au Levain', 'Pain de campagne traditionnel au levain naturel, cuit au four à bois', 3.90, 2, 5, 5, 8, 1, TRUE, 'https://images.unsplash.com/photo-1549931319-a545dcf3bc73?w=800'),
('Baguette Tradition', 'Baguette tradition française croustillante, Label Boulanger', 1.20, 2, 5, 5, 8, 1, TRUE, 'https://images.unsplash.com/photo-1509440159596-0249088772ff?w=800'),
('Croissants Pur Beurre', 'Croissants artisanaux pur beurre AOP, sachet de 4', 5.50, 2, 2, 5, 8, 1, TRUE, 'https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=800'),
('Pain aux Céréales', 'Pain complet aux 7 céréales, riche en fibres', 4.20, 2, 5, 5, 8, 1, TRUE, 'https://images.unsplash.com/photo-1585478259715-876acc5be8eb?w=800'),
('Brioche Tressée', 'Brioche moelleuse tressée à l''ancienne, 400g', 5.80, 2, 2, 5, 8, 1, TRUE, 'https://images.unsplash.com/photo-1608198399988-d7f5a8b9764f?w=800'),

-- Shelf 6: Épicerie Fine (Category: Épicerie=9 for dry goods, Charcuterie=5 for terrines/rillettes)
('Miel de Lavande', 'Miel de lavande de Provence, récolte artisanale, pot 500g', 9.90, 2, 2, 6, 9, 1, FALSE, 'https://images.unsplash.com/photo-1587049352846-4a222e784acc?w=800'),
('Confiture de Fraises Maison', 'Confiture artisanale de fraises, 70% de fruits, pot 350g', 5.50, 2, 2, 6, 9, 1, FALSE, 'https://images.unsplash.com/photo-1599490659213-e2b9527bd087?w=800'),
('Huile d''Olive Vierge Extra', 'Huile d''olive première pression à froid, bouteille 750ml', 12.50, 2, 3, 6, 9, 1, FALSE, 'https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=800'),
('Pâtes Fraîches aux Œufs', 'Pâtes fraîches artisanales aux œufs de nos poules, 500g', 4.90, 2, 2, 6, 9, 1, TRUE, 'https://images.unsplash.com/photo-1621996346565-e3dbc646d9a9?w=800'),
('Terrine de Campagne', 'Terrine de campagne maison aux herbes, bocal 180g', 6.80, 2, 2, 6, 5, 1, TRUE, 'https://images.unsplash.com/photo-1544025162-d76694265947?w=800'),
('Rillettes de Canard', 'Rillettes de canard fermier du Sud-Ouest, bocal 200g', 7.90, 2, 2, 6, 5, 1, FALSE, 'https://images.unsplash.com/photo-1574781330855-d0db8cc6a79c?w=800');

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
