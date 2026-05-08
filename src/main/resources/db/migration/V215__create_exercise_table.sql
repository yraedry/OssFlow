CREATE TABLE exercise (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    owner_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    category VARCHAR(30) NOT NULL,
    equipment VARCHAR(30) NOT NULL DEFAULT 'NO_EQUIPMENT',
    youtube_url VARCHAR(500),
    visibility VARCHAR(20) NOT NULL DEFAULT 'PUBLIC',
    created_at VARCHAR(255) NOT NULL,
    updated_at VARCHAR(255) NOT NULL,
    version BIGINT NOT NULL,
    deleted_at VARCHAR(255),
    purge_at VARCHAR(255)
);
