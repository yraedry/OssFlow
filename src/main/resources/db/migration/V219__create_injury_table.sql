CREATE TABLE injury (
    id BIGSERIAL PRIMARY KEY,
    owner_id BIGINT NOT NULL,
    body_part VARCHAR(100) NOT NULL,
    description TEXT,
    severity VARCHAR(20) NOT NULL DEFAULT 'MILD',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    started_on VARCHAR(20),
    recovered_on VARCHAR(20),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL
);
