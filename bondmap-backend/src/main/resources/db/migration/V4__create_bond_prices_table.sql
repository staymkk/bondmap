CREATE TABLE bond_prices (
                             id BIGSERIAL PRIMARY KEY,

                             bond_id BIGINT NOT NULL,

                             price DOUBLE PRECISION NOT NULL,

                             price_date DATE NOT NULL,

                             CONSTRAINT fk_bond_prices_bond
                                 FOREIGN KEY (bond_id)
                                     REFERENCES bonds(id)
                                     ON DELETE CASCADE
);