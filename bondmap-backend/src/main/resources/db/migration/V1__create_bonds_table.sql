CREATE TABLE bonds (
                       id BIGSERIAL PRIMARY KEY,
                       ticker VARCHAR(255) NOT NULL,
                       name VARCHAR(255) NOT NULL,
                       nominal DOUBLE PRECISION NOT NULL,
                       coupon_rate DOUBLE PRECISION NOT NULL
);