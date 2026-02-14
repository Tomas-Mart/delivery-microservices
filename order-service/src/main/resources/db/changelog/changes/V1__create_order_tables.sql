--liquefaction formatted sql

--changeset author:1
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    courier_id VARCHAR(50),
    zone_id VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_orders_zone_status ON orders(zone_id, status);