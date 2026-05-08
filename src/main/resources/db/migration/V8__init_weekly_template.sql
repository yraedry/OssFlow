CREATE TABLE weekly_template (
    id          BIGSERIAL PRIMARY KEY,
    owner_id    INTEGER NOT NULL UNIQUE,
    days_json   TEXT,
    created_at  TEXT    NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ','now')),
    updated_at  TEXT    NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ','now')),
    version     INTEGER NOT NULL DEFAULT 0,
    deleted_at  TEXT,
    purge_at    TEXT
);

CREATE UNIQUE INDEX idx_weekly_template_owner ON weekly_template(owner_id);
