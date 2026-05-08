CREATE TABLE system (
    id                   BIGSERIAL PRIMARY KEY,
    owner_id             BIGINT       NOT NULL DEFAULT 1,
    name                 VARCHAR(120) NOT NULL,
    description          TEXT,
    anchor_position_id   BIGINT,
    flow_definition      TEXT         NOT NULL,
    flow_schema_version  VARCHAR(10)  NOT NULL DEFAULT 'v1',
    visibility           VARCHAR(10)  NOT NULL DEFAULT 'PRIVATE',
    created_at           TIMESTAMP    NOT NULL,
    updated_at           TIMESTAMP    NOT NULL,
    version              BIGINT       NOT NULL DEFAULT 0,
    deleted_at           TIMESTAMP,
    purge_at             TIMESTAMP,
    FOREIGN KEY (anchor_position_id) REFERENCES position(id)
);
CREATE UNIQUE INDEX ux_system_owner_name_active ON system(owner_id, name) WHERE deleted_at IS NULL;
