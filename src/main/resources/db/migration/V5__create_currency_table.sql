-- Create Currency table
CREATE TABLE IF NOT EXISTS currency (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(3) NOT NULL UNIQUE,
    label VARCHAR(100) NOT NULL,
    usd_exchange_rate DECIMAL(20, 10) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- Create index on is_deleted for soft delete queries
CREATE INDEX idx_currency_is_deleted ON currency(is_deleted);

-- Create index on code for faster lookups
CREATE INDEX idx_currency_code ON currency(code) WHERE is_deleted = FALSE;

-- Create trigger to automatically update updated_at
CREATE OR REPLACE FUNCTION update_currency_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER currency_updated_at_trigger
    BEFORE UPDATE ON currency
    FOR EACH ROW
    EXECUTE FUNCTION update_currency_updated_at();

-- Add comment to table
COMMENT ON TABLE currency IS 'Stores currency information and exchange rates relative to USD';
COMMENT ON COLUMN currency.code IS 'ISO 4217 currency code (e.g., USD, EUR, GBP)';
COMMENT ON COLUMN currency.label IS 'Currency display name';
COMMENT ON COLUMN currency.usd_exchange_rate IS 'Exchange rate relative to 1 USD (e.g., 0.85 means 1 USD = 0.85 of this currency)';
