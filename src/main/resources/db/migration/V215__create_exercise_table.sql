CREATE TABLE exercise (
    id BIGSERIAL PRIMARY KEY,
    owner_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    category VARCHAR(30) NOT NULL,
    equipment VARCHAR(30) NOT NULL DEFAULT 'NO_EQUIPMENT',
    youtube_url VARCHAR(500),
    visibility VARCHAR(20) NOT NULL DEFAULT 'PUBLIC',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL,
    deleted_at TIMESTAMP,
    purge_at TIMESTAMP
);
