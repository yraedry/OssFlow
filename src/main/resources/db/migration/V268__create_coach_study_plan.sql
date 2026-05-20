-- Planes de estudio del maestro para el atleta
CREATE TABLE coach_study_plan (
    id              BIGSERIAL PRIMARY KEY,
    coach_id        BIGINT        NOT NULL REFERENCES account(id),
    athlete_id      BIGINT        NOT NULL REFERENCES account(id),
    title           VARCHAR(200)  NOT NULL,
    description     TEXT,
    status          VARCHAR(20)   NOT NULL DEFAULT 'DRAFT',
    viewed_by_athlete BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_coach_study_plan_coach    ON coach_study_plan(coach_id, athlete_id, created_at DESC);
CREATE INDEX idx_coach_study_plan_athlete  ON coach_study_plan(athlete_id, status) WHERE status = 'PUBLISHED';

-- Bloques dentro de un plan del maestro
CREATE TABLE coach_study_block (
    id              BIGSERIAL PRIMARY KEY,
    plan_id         BIGINT        NOT NULL REFERENCES coach_study_plan(id) ON DELETE CASCADE,
    title           VARCHAR(200)  NOT NULL DEFAULT '',
    block_order     INT           NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_coach_study_block_plan ON coach_study_block(plan_id, block_order);

-- Items dentro de un bloque (texto libre o técnica del catálogo)
CREATE TABLE coach_study_item (
    id              BIGSERIAL PRIMARY KEY,
    block_id        BIGINT        NOT NULL REFERENCES coach_study_block(id) ON DELETE CASCADE,
    item_order      INT           NOT NULL DEFAULT 0,
    item_type       VARCHAR(20)   NOT NULL DEFAULT 'TEXT',  -- TEXT | TECHNIQUE
    content         TEXT,                                    -- texto libre
    technique_id    BIGINT        REFERENCES technique(id),  -- si item_type = TECHNIQUE
    technique_name  VARCHAR(255),                            -- snapshot del nombre al añadir
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_coach_study_item_block ON coach_study_item(block_id, item_order);
