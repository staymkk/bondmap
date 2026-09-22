CREATE TABLE key_rates (
    id BIGSERIAL PRIMARY KEY,
    rate DOUBLE PRECISION NOT NULL,
    rate_date DATE NOT NULL UNIQUE
);

INSERT INTO key_rates (rate, rate_date) VALUES
    (16.0, '2024-07-29'),
    (18.0, '2024-10-28'),
    (21.0, '2024-12-20'),
    (21.0, '2025-02-14'),
    (20.0, '2025-06-06'),
    (18.0, '2025-09-12'),
    (17.0, '2026-01-16'),
    (16.0, '2026-04-25'),
    (15.0, '2026-07-25'),
    (14.0, '2026-09-15');
