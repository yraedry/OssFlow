-- V223__physical_session_exercise.sql
-- Tabla join para registrar los ejercicios trabajados en sesiones físicas.

CREATE TABLE physical_session_exercise (
    physical_session_id BIGINT  NOT NULL,
    exercise_id         BIGINT  NOT NULL,
    sets                INTEGER,
    reps                INTEGER,
    duration_seconds    INTEGER,
    notes               TEXT,
    PRIMARY KEY (physical_session_id, exercise_id),
    FOREIGN KEY (physical_session_id) REFERENCES physical_session(id) ON DELETE CASCADE,
    FOREIGN KEY (exercise_id)         REFERENCES exercise(id)
);

CREATE INDEX idx_pse_session ON physical_session_exercise(physical_session_id);
CREATE INDEX idx_pse_exercise ON physical_session_exercise(exercise_id);
