CREATE TABLE position (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    owner_id        BIGINT       NOT NULL DEFAULT 1,
    name            VARCHAR(120) NOT NULL,
    type            VARCHAR(30)  NOT NULL,
    description     TEXT,
    visibility      VARCHAR(10)  NOT NULL DEFAULT 'PRIVATE',
    created_at      TIMESTAMP    NOT NULL,
    updated_at      TIMESTAMP    NOT NULL,
    version         BIGINT       NOT NULL DEFAULT 0,
    deleted_at      TIMESTAMP,
    purge_at        TIMESTAMP
);

CREATE UNIQUE INDEX ux_position_owner_name_active
    ON position(owner_id, name) WHERE deleted_at IS NULL;
CREATE INDEX ix_position_owner_deleted ON position(owner_id, deleted_at);

CREATE TABLE technique (
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    owner_id            BIGINT       NOT NULL DEFAULT 1,
    name                VARCHAR(120) NOT NULL,
    category            VARCHAR(30)  NOT NULL,
    description         TEXT,
    youtube_url         VARCHAR(500),
    minimum_belt        VARCHAR(15)  NOT NULL,
    modality            VARCHAR(10)  NOT NULL,
    start_position_id   BIGINT       NOT NULL,
    end_position_id     BIGINT,
    visibility          VARCHAR(10)  NOT NULL DEFAULT 'PRIVATE',
    created_at          TIMESTAMP    NOT NULL,
    updated_at          TIMESTAMP    NOT NULL,
    version             BIGINT       NOT NULL DEFAULT 0,
    deleted_at          TIMESTAMP,
    purge_at            TIMESTAMP,
    FOREIGN KEY (start_position_id) REFERENCES position(id),
    FOREIGN KEY (end_position_id)   REFERENCES position(id)
);

CREATE UNIQUE INDEX ux_technique_owner_name_active
    ON technique(owner_id, name) WHERE deleted_at IS NULL;
CREATE INDEX ix_technique_start_position ON technique(start_position_id);
CREATE INDEX ix_technique_end_position   ON technique(end_position_id);
CREATE INDEX ix_technique_category       ON technique(category);
