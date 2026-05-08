CREATE TABLE physical_session (
    id              BIGSERIAL PRIMARY KEY,
    owner_id        INTEGER NOT NULL,
    session_date    DATE    NOT NULL,
    session_type    VARCHAR(20) NOT NULL CHECK (session_type IN ('STRENGTH','CARDIO','FLEXIBILITY','HIIT','OTHER')),
    title           VARCHAR(200) NOT NULL,
    duration_minutes INTEGER,
    notes           TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    version         INTEGER NOT NULL DEFAULT 0,
    deleted_at      TIMESTAMP,
    purge_at        TIMESTAMP
);

CREATE INDEX idx_physical_session_owner_date ON physical_session(owner_id, session_date DESC);
