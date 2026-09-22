-- Seed: diversified bond universe + ~18 months of price history (deterministic)

INSERT INTO bonds (ticker, name, nominal, coupon_rate, maturity_date, currency, coupon_period_days)
SELECT v.ticker, v.name, v.nominal, v.coupon_rate, v.maturity_date::date, v.currency, v.coupon_period_days
FROM (VALUES
    ('RU000A105XX1', 'ОФЗ-ПД 26240', 1000.0, 12.00, '2030-03-15', 'RUB', 182),
    ('RU000A105XX2', 'ОФЗ-ПД 26241', 1000.0, 11.50, '2031-05-20', 'RUB', 182),
    ('RU000A105XX3', 'ОФЗ-ПД 26242', 1000.0, 13.20, '2032-08-10', 'RUB', 182),
    ('RU000A105XX4', 'ОФЗ-ПД 26243', 1000.0, 14.00, '2033-11-25', 'RUB', 182),
    ('RU000A105XX5', 'ОФЗ-ПД 26244', 1000.0, 10.80, '2029-02-28', 'RUB', 182),
    ('RU000A105XX6', 'ОФЗ-ПД 26245', 1000.0, 15.10, '2035-06-18', 'RUB', 182),
    ('RU000A105XX7', 'ОФЗ-ПК 29020', 1000.0, 16.50, '2028-09-12', 'RUB', 182),
    ('RU000A105XX8', 'ОФЗ-ПК 29021', 1000.0, 15.80, '2027-12-01', 'RUB', 182),
    ('RU000A106AA1', 'Сбербанк БО-001Р-42', 1000.0, 14.50, '2029-04-15', 'RUB', 182),
    ('RU000A106AA2', 'Сбербанк БО-001Р-45', 1000.0, 13.90, '2030-07-22', 'RUB', 182),
    ('RU000A106BB1', 'ВТБ БО-43', 1000.0, 15.20, '2028-11-30', 'RUB', 182),
    ('RU000A106BB2', 'ВТБ БО-48', 1000.0, 14.80, '2031-01-20', 'RUB', 182),
    ('RU000A106CC1', 'Газпром капитал БО-001Р-10', 1000.0, 13.40, '2030-09-05', 'RUB', 182),
    ('RU000A106CC2', 'Газпром капитал БО-001Р-12', 1000.0, 12.90, '2032-03-18', 'RUB', 182),
    ('RU000A106DD1', 'Роснефть-001Р-10', 1000.0, 14.10, '2029-08-14', 'RUB', 182),
    ('RU000A106DD2', 'Роснефть-001Р-14', 1000.0, 13.70, '2031-10-09', 'RUB', 182),
    ('RU000A106EE1', 'РЖД 001Р-28R', 1000.0, 12.50, '2030-05-27', 'RUB', 182),
    ('RU000A106EE2', 'РЖД 001Р-32R', 1000.0, 13.00, '2033-02-11', 'RUB', 182),
    ('RU000A106FF1', 'МТС 001Р-20', 1000.0, 11.80, '2028-06-19', 'RUB', 182),
    ('RU000A106FF2', 'МТС 001Р-24', 1000.0, 12.20, '2030-12-03', 'RUB', 182),
    ('RU000A106GG1', 'Яндекс БО-01', 1000.0, 16.00, '2027-09-28', 'RUB', 182),
    ('RU000A106GG2', 'Яндекс БО-03', 1000.0, 15.40, '2029-01-16', 'RUB', 182),
    ('RU000A106HH1', 'Альфа-Банк БО-17', 1000.0, 14.20, '2028-03-07', 'RUB', 182),
    ('RU000A106HH2', 'Альфа-Банк БО-21', 1000.0, 13.60, '2030-10-21', 'RUB', 182),
    ('RU000A106II1', 'Московский кредитный банк БО-001Р-15', 1000.0, 15.00, '2028-08-25', 'RUB', 182),
    ('RU000A106JJ1', 'ПИК-Корпорация 001Р-04', 1000.0, 17.20, '2027-05-13', 'RUB', 182),
    ('RU000A106KK1', 'Лукойл-001Р-06', 1000.0, 12.70, '2031-04-30', 'RUB', 182),
    ('RU000A106LL1', 'Новатэк БО-001Р-03', 1000.0, 13.10, '2032-07-08', 'RUB', 182),
    ('XS0123456789', 'Gazprom Eurobond 2030', 1000.0, 5.25, '2030-01-15', 'USD', 182),
    ('XS0987654321', 'Sberbank Eurobond 2029', 1000.0, 4.80, '2029-06-20', 'USD', 182),
    ('XS1122334455', 'RZD Eurobond 2031', 1000.0, 5.50, '2031-11-10', 'USD', 182),
    ('RU000A106MM1', 'ОФЗ-ПД 26250', 1000.0, 9.50, '2027-03-22', 'RUB', 182),
    ('RU000A106NN1', 'ОФЗ-ПД 26251', 1000.0, 10.20, '2028-10-17', 'RUB', 182),
    ('RU000A106OO1', 'ОФЗ-ИН 52002', 1000.0, 2.50, '2032-02-02', 'RUB', 182),
    ('RU000A106PP1', 'Совкомбанк БО-001Р-08', 1000.0, 16.80, '2027-11-04', 'RUB', 182)
) AS v(ticker, name, nominal, coupon_rate, maturity_date, currency, coupon_period_days)
WHERE NOT EXISTS (
    SELECT 1 FROM bonds b WHERE b.ticker = v.ticker
);

-- Historical prices for bonds with little/no history (bi-weekly, ~18 months)
INSERT INTO bond_prices (bond_id, price, price_date)
SELECT
    b.id,
    ROUND(
        (
            b.nominal * (
                0.90
                + ((b.id * 13 + EXTRACT(DOY FROM d.day)::int) % 25) / 100.0
                + 0.04 * SIN((EXTRACT(EPOCH FROM d.day) / 86400.0 + b.id) / 20.0)
            )
        )::numeric,
        2
    )::double precision,
    d.day::date
FROM bonds b
CROSS JOIN generate_series(
    DATE '2025-03-01',
    DATE '2026-09-15',
    INTERVAL '14 days'
) AS d(day)
WHERE (
    SELECT COUNT(*) FROM bond_prices bp WHERE bp.bond_id = b.id
) < 5
AND NOT EXISTS (
    SELECT 1
    FROM bond_prices existing
    WHERE existing.bond_id = b.id
      AND existing.price_date = d.day::date
);
