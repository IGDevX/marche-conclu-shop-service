-- Seed categories for dev-local environment

INSERT INTO category (name, slug, description, display_order) VALUES
('Fruits', 'fruits',
 'Tous les fruits frais, locaux et de saison. Exemples : pommes, poires, fraises, framboises, cerises, abricots, pêches, prunes, raisins, melons, pastèques, kiwis, agrumes (oranges, citrons, pamplemousses).',
 1),

('Légumes', 'legumes',
 'Légumes frais cultivés localement. Exemples : tomates, concombres, salades, épinards, courgettes, aubergines, poivrons, carottes, pommes de terre, oignons, ail, poireaux, choux, brocolis, haricots verts, petits pois, courges, potirons.',
 2),

('Viandes', 'viandes',
 'Viandes fraîches d''élevages locaux. Exemples : bœuf (steaks, rôtis, viande hachée), veau, porc (côtelettes, rôtis, saucisses), agneau, volailles (poulet, dinde, canard, pintade, oie), lapin, gibier.',
 3),

('Poissons', 'poissons',
 'Poissons et fruits de mer frais. Exemples : saumon, truite, dorade, bar, cabillaud, lieu, sole, maquereau, sardines, thon, crevettes, moules, huîtres, coquilles Saint-Jacques, homard, crabe.',
 4),

('Charcuterie', 'charcuterie',
 'Charcuterie artisanale et produits de salaison. Exemples : jambon blanc, jambon cru, saucisson sec, saucisses, pâtés, rillettes, terrines, boudins, lardons, bacon, pancetta, chorizo.',
 5),

('Produits laitiers', 'produits-laitiers',
 'Produits laitiers fermiers et artisanaux. Exemples : lait (entier, demi-écrémé, écrémé), yaourts nature et aromatisés, fromages blancs, faisselles, crèmes fraîches, beurre, fromages (pâte molle, pâte pressée, pâte persillée, fromages de chèvre, fromages de brebis).',
 6),

('Œufs', 'oeufs',
 'Œufs frais de poules élevées en plein air ou bio. Exemples : œufs de poules, œufs de cailles, œufs de canes, œufs bio, œufs Label Rouge, œufs plein air.',
 7),

('Pain & Farines', 'pain-farines',
 'Pains artisanaux et farines pour la boulangerie. Exemples : baguettes, pains de campagne, pains complets, pains aux céréales, pains de seigle, brioches, viennoiseries, farines (blé, seigle, épeautre, sarrasin), levures.',
 8),

('Épicerie', 'epicerie',
 'Produits d''épicerie sèche et conserves. Exemples : pâtes, riz, quinoa, légumineuses (lentilles, pois chiches, haricots), huiles (olive, tournesol, colza), vinaigres, conserves de légumes, conserves de poissons, confitures, miels, sucre, sel, épices, herbes séchées.',
 9),

('Condiments', 'condiments',
 'Sauces, condiments et assaisonnements. Exemples : moutarde, ketchup, mayonnaise, sauce soja, sauce tomate, pesto, tapenade, cornichons, câpres, olives, pickles, chutneys.',
 10),

('Boissons', 'boissons',
 'Boissons diverses sans alcool et jus. Exemples : jus de fruits frais, jus de légumes, smoothies, sirops, sodas artisanaux, eaux minérales, thés, tisanes, cafés, chocolats chauds.',
 11),

('Pâtisserie & Desserts', 'patisserie-desserts',
 'Pâtisseries artisanales et desserts. Exemples : gâteaux, tartes, tartelettes, éclairs, macarons, cookies, brownies, madeleines, financiers, mousses, crèmes desserts, compotes, glaces, sorbets.',
 12),

('Autres produits', 'autres-produits',
 'Autres produits alimentaires et spécialités. Exemples : plats préparés, soupes, bouillons, sauces cuisinées, produits végétariens/vegan, produits sans gluten, produits bio spécifiques, fleurs comestibles, champignons.',
 13)

ON CONFLICT (slug) WHERE (is_deleted = FALSE) DO NOTHING;

COMMENT ON TABLE category IS 'Categories seeded for dev-local environment with full descriptions';

