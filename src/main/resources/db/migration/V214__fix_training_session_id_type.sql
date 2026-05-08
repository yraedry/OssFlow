-- La columna id fue creada sin tipo en SQLite, lo que hace que Hibernate no pueda
-- autogenerar el ID y la columna queda NULL en inserciones nuevas.
-- SQLite no permite ALTER COLUMN, hay que recrear la tabla.
-- En SQLite AUTOINCREMENT solo funciona con INTEGER PRIMARY KEY (no BIGINT).

CREATE TABLE training_session_new (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    owner_id    BIGINT NOT NULL,
    session_date VARCHAR(255) NOT NULL,
    duration_minutes INTEGER NOT NULL,
    location    VARCHAR(200),
    intensity   VARCHAR(15) NOT NULL CHECK (intensity IN ('LIGHT','MODERATE','HARD','COMPETITION')),
    session_type VARCHAR(20) NOT NULL DEFAULT 'BJJ',
    notes_markdown TEXT,
    created_at  VARCHAR(255) NOT NULL,
    updated_at  VARCHAR(255) NOT NULL,
    deleted_at  VARCHAR(255),
    purge_at    VARCHAR(255),
    version     BIGINT NOT NULL
);

INSERT INTO training_session_new
    (id, owner_id, session_date, duration_minutes, location, intensity, session_type,
     notes_markdown, created_at, updated_at, deleted_at, purge_at, version)
SELECT
    COALESCE(id, rowid),
    owner_id, session_date, duration_minutes, location, intensity, session_type,
    notes_markdown, created_at, updated_at, deleted_at, purge_at, version
FROM training_session;

DROP TABLE training_session;
ALTER TABLE training_session_new RENAME TO training_session;
