-- Test mock data for units (minimal for testing)
INSERT INTO units (code, label) VALUES
    ('kg', 'Kilogram'),
    ('pc', 'Piece')
ON CONFLICT (code) DO NOTHING;
