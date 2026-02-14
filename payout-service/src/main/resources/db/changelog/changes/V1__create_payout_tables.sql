CREATE TABLE pending_payouts (
    id BIGSERIAL PRIMARY KEY,
    courier_id VARCHAR(50) NOT NULL,
    order_id BIGINT NOT NULL,
    amount DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE processed_events (
    event_id VARCHAR(100) PRIMARY KEY,
    processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);