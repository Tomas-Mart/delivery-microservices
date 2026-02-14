CREATE TABLE sessions (
    id BIGSERIAL PRIMARY KEY,
    courier_id VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL, -- ACTIVE, PAUSED, CLOSED
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    version INT DEFAULT 0 -- для optimistic lock
);

CREATE INDEX idx_sessions_courier_status ON sessions(courier_id, status);