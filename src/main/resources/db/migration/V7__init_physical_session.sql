CREATE TABLE physical_session (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    owner_id        INTEGER NOT NULL,
    session_date    TEXT    NOT NULL,
    session_type    TEXT    NOT NULL CHECK (session_type IN ('STRENGTH','CARDIO','FLEXIBILITY','HIIT','OTHER')),
    title           TEXT    NOT NULL,
    duration_minutes INTEGER,
    notes           TEXT,
    created_at      TEXT    NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ','now')),
    updated_at      TEXT    NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ','now')),
    version         INTEGER NOT NULL DEFAULT 0,
    deleted_at      TEXT,
    purge_at        TEXT
);

CREATE INDEX idx_physical_session_owner_date ON physical_session(owner_id, session_date DESC);
