CREATE TABLE study_plan (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    owner_id        BIGINT NOT NULL DEFAULT 1,
    title           VARCHAR(200) NOT NULL,
    goal_markdown   TEXT,
    start_date      DATE NOT NULL,
    end_date        DATE NOT NULL,
    status          VARCHAR(15) NOT NULL,
    created_at      TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP NOT NULL,
    version         BIGINT NOT NULL DEFAULT 0,
    deleted_at      TIMESTAMP,
    purge_at        TIMESTAMP
);

CREATE TABLE study_block (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    study_plan_id   BIGINT NOT NULL,
    title           VARCHAR(200) NOT NULL,
    start_date      DATE NOT NULL,
    end_date        DATE NOT NULL,
    block_order     INTEGER NOT NULL,
    notes_markdown  TEXT,
    focus_entities  TEXT,
    FOREIGN KEY (study_plan_id) REFERENCES study_plan(id) ON DELETE CASCADE
);

CREATE TABLE study_item (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    study_block_id   BIGINT NOT NULL,
    description      VARCHAR(500) NOT NULL,
    status           VARCHAR(15) NOT NULL,
    target_type      VARCHAR(20),
    target_id        BIGINT,
    due_date         DATE,
    ai_generated     BOOLEAN NOT NULL DEFAULT 0,
    completed_at     TIMESTAMP,
    created_at       TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP NOT NULL,
    version          BIGINT NOT NULL DEFAULT 0,
    FOREIGN KEY (study_block_id) REFERENCES study_block(id) ON DELETE CASCADE
);
