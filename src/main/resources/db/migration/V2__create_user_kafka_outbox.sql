CREATE TABLE IF NOT EXISTS user_kafka_outbox(
    id BIGINT PRIMARY KEY,
    event_type VARCHAR(200) NOT NULL,
    payload CLOB NOT NULL,
    is_consumed BOOLEAN DEFAULT FALSE
);