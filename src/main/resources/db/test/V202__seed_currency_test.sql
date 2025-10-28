-- Seed data for Currency table (Test environment)
-- Exchange rates are approximate and for testing purposes only

INSERT INTO currency (code, label, usd_exchange_rate) VALUES
('USD', 'US Dollar', 1.00),
('EUR', 'Euro', 0.92),
('GBP', 'British Pound', 0.79),
('JPY', 'Japanese Yen', 149.50),
('CHF', 'Swiss Franc', 0.88),
('CAD', 'Canadian Dollar', 1.36),
('AUD', 'Australian Dollar', 1.53),
('CNY', 'Chinese Yuan', 7.24),
('INR', 'Indian Rupee', 83.12),
('BRL', 'Brazilian Real', 4.97);

-- Add comment
COMMENT ON TABLE currency IS 'Test seed data for currencies with exchange rates relative to USD (1 USD = X currency units)';
