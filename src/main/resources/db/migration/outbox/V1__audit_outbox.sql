CREATE TABLE audit_outbox(
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(50),
    aggregate_id VARCHAR(100),
    event_type VARCHAR(50),
    payload JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);