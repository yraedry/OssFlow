CREATE TABLE tag (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(60) NOT NULL UNIQUE,
    created_at  TIMESTAMP NOT NULL
);

CREATE TABLE note (
    id              BIGSERIAL PRIMARY KEY,
    owner_id        BIGINT NOT NULL DEFAULT 1,
    title           VARCHAR(200) NOT NULL,
    body_markdown   TEXT NOT NULL,
    target_type     VARCHAR(20),
    target_id       BIGINT,
    created_at      TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP NOT NULL,
    version         BIGINT NOT NULL DEFAULT 0,
    deleted_at      TIMESTAMP,
    purge_at        TIMESTAMP
);
CREATE INDEX ix_note_owner_target ON note(owner_id, target_type, target_id);
CREATE INDEX ix_note_owner_created ON note(owner_id, created_at);

CREATE TABLE note_tag (
    note_id  BIGINT NOT NULL,
    tag_id   BIGINT NOT NULL,
    PRIMARY KEY (note_id, tag_id),
    FOREIGN KEY (note_id) REFERENCES note(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id)  REFERENCES tag(id)  ON DELETE CASCADE
);

CREATE TABLE training_session (
    id                BIGSERIAL PRIMARY KEY,
    owner_id          BIGINT NOT NULL DEFAULT 1,
    session_date      DATE NOT NULL,
    duration_minutes  INTEGER NOT NULL,
    location          VARCHAR(120),
    intensity         VARCHAR(15) NOT NULL,
    notes_markdown    TEXT,
    created_at        TIMESTAMP NOT NULL,
    updated_at        TIMESTAMP NOT NULL,
    version           BIGINT NOT NULL DEFAULT 0,
    deleted_at        TIMESTAMP,
    purge_at          TIMESTAMP
);
CREATE INDEX ix_training_session_owner_date ON training_session(owner_id, session_date DESC);

CREATE TABLE training_session_technique (
    training_session_id  BIGINT NOT NULL,
    technique_id         BIGINT NOT NULL,
    rep_count            INTEGER,
    notes_markdown       TEXT,
    PRIMARY KEY (training_session_id, technique_id),
    FOREIGN KEY (training_session_id) REFERENCES training_session(id) ON DELETE CASCADE,
    FOREIGN KEY (technique_id)        REFERENCES technique(id)
);

CREATE TABLE competition_log (
    id                  BIGSERIAL PRIMARY KEY,
    owner_id            BIGINT NOT NULL DEFAULT 1,
    event_name          VARCHAR(200) NOT NULL,
    event_date          DATE NOT NULL,
    weight_category     VARCHAR(30),
    total_matches       INTEGER NOT NULL DEFAULT 0,
    result              VARCHAR(15),
    analysis_markdown   TEXT,
    created_at          TIMESTAMP NOT NULL,
    updated_at          TIMESTAMP NOT NULL,
    version             BIGINT NOT NULL DEFAULT 0,
    deleted_at          TIMESTAMP,
    purge_at            TIMESTAMP
);

CREATE TABLE competition_match (
    id                       BIGSERIAL PRIMARY KEY,
    competition_log_id       BIGINT NOT NULL,
    match_order              INTEGER NOT NULL,
    opponent_name            VARCHAR(120) NOT NULL,
    opponent_team            VARCHAR(120),
    outcome                  VARCHAR(15) NOT NULL,
    method                   VARCHAR(30) NOT NULL,
    submission_technique_id  BIGINT,
    notes_markdown           TEXT,
    UNIQUE (competition_log_id, match_order),
    FOREIGN KEY (competition_log_id)      REFERENCES competition_log(id) ON DELETE CASCADE,
    FOREIGN KEY (submission_technique_id) REFERENCES technique(id)
);
