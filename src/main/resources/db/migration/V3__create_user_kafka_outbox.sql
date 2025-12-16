CREATE TABLE IF NOT EXISTS user_kafka_outbox(
    id VARCHAR(255) PRIMARY KEY,
    event_type VARCHAR(200) NOT NULL,
    user_payload CLOB NOT NULL,
    is_consumed BOOLEAN DEFAULT FALSE
);