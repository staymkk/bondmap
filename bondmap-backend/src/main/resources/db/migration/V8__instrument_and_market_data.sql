ALTER TABLE bonds
    ADD COLUMN IF NOT EXISTS isin VARCHAR(32),
    ADD COLUMN IF NOT EXISTS bond_type VARCHAR(32);

UPDATE bonds
SET isin = ticker
WHERE isin IS NULL OR btrim(isin) = '';

UPDATE bonds
SET bond_type = CASE
    WHEN name ILIKE 'ОФЗ%' THEN 'GOVERNMENT'
    WHEN currency IN ('USD', 'EUR') THEN 'EUROBOND'
    ELSE 'CORPORATE'
END
WHERE bond_type IS NULL OR btrim(bond_type) = '';

UPDATE bonds
SET ticker = substring(name from '([0-9]{5,6})')
WHERE name ILIKE 'ОФЗ%'
  AND name ~ '[0-9]{5,6}';

ALTER TABLE bonds
    ALTER COLUMN isin SET DEFAULT '',
    ALTER COLUMN bond_type SET DEFAULT 'CORPORATE';

UPDATE bonds SET isin = COALESCE(isin, '');
UPDATE bonds SET bond_type = COALESCE(bond_type, 'CORPORATE');

ALTER TABLE bonds
    ALTER COLUMN isin SET NOT NULL,
    ALTER COLUMN bond_type SET NOT NULL;

ALTER TABLE bond_prices
    ADD COLUMN IF NOT EXISTS open_price DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS high_price DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS low_price DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS close_price DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS volume DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS source VARCHAR(32) NOT NULL DEFAULT 'SIMULATION';

UPDATE bond_prices
SET close_price = price
WHERE close_price IS NULL;

WITH ordered AS (
    SELECT
        id,
        price,
        LAG(price) OVER (PARTITION BY bond_id ORDER BY price_date) AS prev_close
    FROM bond_prices
)
UPDATE bond_prices bp
SET
    open_price = COALESCE(ordered.prev_close, bp.price),
    close_price = bp.price,
    high_price = GREATEST(COALESCE(ordered.prev_close, bp.price), bp.price)
        * (1.0 + 0.0025 + ((bp.id % 9) * 0.0004)),
    low_price = LEAST(COALESCE(ordered.prev_close, bp.price), bp.price)
        * (1.0 - 0.0025 - ((bp.id % 7) * 0.0003)),
    volume = 800000 + ((bp.id % 80) * 15000) + ((EXTRACT(DOY FROM bp.price_date)::int % 25) * 8000),
    source = COALESCE(bp.source, 'SIMULATION')
FROM ordered
WHERE bp.id = ordered.id;
