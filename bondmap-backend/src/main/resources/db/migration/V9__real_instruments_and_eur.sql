UPDATE bonds
SET
    name = 'ОФЗ-ПД 26243',
    isin = 'RU000A1038V6',
    ticker = '26243',
    bond_type = 'GOVERNMENT',
    nominal = 1000.0,
    coupon_rate = 14.00,
    maturity_date = DATE '2038-05-19',
    currency = 'RUB',
    coupon_period_days = 182
WHERE name = 'ОФЗ-ПД 26243'
   OR isin IN ('RU000A105XX4', 'RU000A1038V6')
   OR ticker IN ('RU000A105XX4', '26243');

UPDATE bond_prices bp
SET
    price = 945.80,
    close_price = 945.80,
    open_price = COALESCE(open_price, 948.10),
    high_price = GREATEST(COALESCE(open_price, 948.10), 945.80, COALESCE(high_price, 952.00)),
    low_price = LEAST(COALESCE(open_price, 948.10), 945.80, COALESCE(low_price, 941.00)),
    source = 'SIMULATION'
FROM bonds b
WHERE bp.bond_id = b.id
  AND b.isin = 'RU000A1038V6'
  AND bp.price_date = (
      SELECT MAX(bp2.price_date)
      FROM bond_prices bp2
      WHERE bp2.bond_id = b.id
  );

INSERT INTO bonds (ticker, name, isin, bond_type, nominal, coupon_rate, maturity_date, currency, coupon_period_days)
SELECT v.ticker, v.name, v.isin, v.bond_type, v.nominal, v.coupon_rate, v.maturity_date::date, v.currency, v.coupon_period_days
FROM (VALUES
    ('2031', 'VEB EUR 2031', 'XS2197674765', 'EUROBOND', 1000.0, 3.15, '2031-06-16', 'EUR', 365),
    ('2028', 'Gazprom EUR 2028', 'XS1721463509', 'EUROBOND', 1000.0, 2.95, '2028-11-21', 'EUR', 365)
) AS v(ticker, name, isin, bond_type, nominal, coupon_rate, maturity_date, currency, coupon_period_days)
WHERE NOT EXISTS (
    SELECT 1 FROM bonds b WHERE b.isin = v.isin
);

INSERT INTO bond_prices (bond_id, price, price_date, open_price, high_price, low_price, close_price, volume, source)
SELECT
    b.id,
    ROUND(
        (
            b.nominal * (
                0.88
                + ((b.id * 11 + EXTRACT(DOY FROM d.day)::int) % 22) / 100.0
                + 0.03 * SIN((EXTRACT(EPOCH FROM d.day) / 86400.0 + b.id) / 18.0)
            )
        )::numeric,
        2
    )::double precision,
    d.day::date,
    NULL, NULL, NULL, NULL,
    650000 + ((b.id % 40) * 12000),
    'SIMULATION'
FROM bonds b
CROSS JOIN generate_series(
    DATE '2025-03-01',
    DATE '2026-09-15',
    INTERVAL '14 days'
) AS d(day)
WHERE b.currency = 'EUR'
AND NOT EXISTS (
    SELECT 1
    FROM bond_prices existing
    WHERE existing.bond_id = b.id
      AND existing.price_date = d.day::date
);

WITH ordered AS (
    SELECT
        id,
        price,
        LAG(price) OVER (PARTITION BY bond_id ORDER BY price_date) AS prev_close
    FROM bond_prices
    WHERE open_price IS NULL OR close_price IS NULL
)
UPDATE bond_prices bp
SET
    open_price = COALESCE(bp.open_price, ordered.prev_close, bp.price),
    close_price = COALESCE(bp.close_price, bp.price),
    high_price = COALESCE(
        bp.high_price,
        GREATEST(COALESCE(ordered.prev_close, bp.price), bp.price) * 1.004
    ),
    low_price = COALESCE(
        bp.low_price,
        LEAST(COALESCE(ordered.prev_close, bp.price), bp.price) * 0.996
    ),
    volume = COALESCE(bp.volume, 700000),
    source = COALESCE(bp.source, 'SIMULATION')
FROM ordered
WHERE bp.id = ordered.id;
