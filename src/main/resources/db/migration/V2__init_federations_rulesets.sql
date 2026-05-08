CREATE TABLE federation (
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(20)  NOT NULL UNIQUE,
    name            VARCHAR(120) NOT NULL,
    official_url    VARCHAR(500),
    created_at      TIMESTAMP    NOT NULL,
    updated_at      TIMESTAMP    NOT NULL,
    version         BIGINT       NOT NULL DEFAULT 0
);

CREATE TABLE ruleset (
    id               BIGSERIAL PRIMARY KEY,
    federation_id    BIGINT       NOT NULL,
    belt             VARCHAR(15)  NOT NULL,
    modality         VARCHAR(10)  NOT NULL,
    effective_from   DATE         NOT NULL,
    effective_to     DATE,
    source_url       VARCHAR(500),
    created_at       TIMESTAMP    NOT NULL,
    updated_at       TIMESTAMP    NOT NULL,
    version          BIGINT       NOT NULL DEFAULT 0,
    FOREIGN KEY (federation_id) REFERENCES federation(id),
    UNIQUE (federation_id, belt, modality, effective_from)
);

CREATE TABLE ruleset_technique (
    ruleset_id        BIGINT       NOT NULL,
    technique_id      BIGINT       NOT NULL,
    status            VARCHAR(20)  NOT NULL,
    condition_notes   TEXT,
    PRIMARY KEY (ruleset_id, technique_id),
    FOREIGN KEY (ruleset_id) REFERENCES ruleset(id) ON DELETE CASCADE,
    FOREIGN KEY (technique_id) REFERENCES technique(id)
);
