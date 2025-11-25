-- Dev-local mock data for units
INSERT INTO units (code, label) VALUES
    ('kg', 'Kilogram'),
    ('g', 'Gram'),
    ('l', 'Liter'),
    ('ml', 'Milliliter'),
    ('pc', 'Piece'),
    ('box', 'Box'),
    ('dozen', 'Dozen'),
    ('bag', 'Bag')
ON CONFLICT (code) WHERE (is_deleted = FALSE) DO NOTHING;
