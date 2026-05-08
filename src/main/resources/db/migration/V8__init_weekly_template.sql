CREATE TABLE weekly_template (
    id          BIGSERIAL PRIMARY KEY,
    owner_id    INTEGER NOT NULL UNIQUE,
    days_json   TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    version     INTEGER NOT NULL DEFAULT 0,
    deleted_at  TIMESTAMP,
    purge_at    TIMESTAMP
);

CREATE UNIQUE INDEX idx_weekly_template_owner ON weekly_template(owner_id);
