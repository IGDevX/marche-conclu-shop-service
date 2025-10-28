-- Seed data for Currency table (Dev-Local environment)
-- Exchange rates are approximate and for development purposes only

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
('BRL', 'Brazilian Real', 4.97),
('MXN', 'Mexican Peso', 17.08),
('KRW', 'South Korean Won', 1320.45),
('SGD', 'Singapore Dollar', 1.34),
('NZD', 'New Zealand Dollar', 1.65),
('SEK', 'Swedish Krona', 10.65),
('NOK', 'Norwegian Krone', 10.85),
('DKK', 'Danish Krone', 6.88),
('PLN', 'Polish Zloty', 3.98),
('THB', 'Thai Baht', 35.20),
('ZAR', 'South African Rand', 18.75);

-- Add comment
COMMENT ON TABLE currency IS 'Dev-local seed data for currencies with exchange rates relative to USD (1 USD = X currency units)';
