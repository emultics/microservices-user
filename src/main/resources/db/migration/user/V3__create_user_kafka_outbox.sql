CREATE TABLE IF NOT EXISTS user_kafka_outbox (
    id UUID PRIMARY KEY,
    event_id VARCHAR(100) NOT NULL,
    aggregate_id UUID NOT NULL,
    aggregate_type VARCHAR(50) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    payload JSONB NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_outbox_event UNIQUE (event_id)
);

CREATE INDEX idx_outbox_aggregate ON user_kafka_outbox (aggregate_id);
CREATE INDEX idx_outbox_event_type ON user_kafka_outbox (event_type);
CREATE INDEX idx_outbox_created_at ON user_kafka_outbox (created_at);