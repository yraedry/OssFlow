-- V230__add_missing_indexes.sql
-- Índices de rendimiento para columnas de filtrado frecuente que faltaban.
-- Se usan CONCURRENTLY + IF NOT EXISTS para evitar bloqueos y ser idempotentes.
-- NOTA: CREATE INDEX CONCURRENTLY no puede ejecutarse dentro de una transacción
--       explícita; Flyway ejecuta cada script en modo autocommit cuando
--       detecta CONCURRENTLY, por lo que este archivo debe mantenerse sin
--       BEGIN/COMMIT explícitos.

-- ─── technique ───────────────────────────────────────────────────────────────
-- owner_id solo (la unique partial cubre (owner_id, name) pero no owner_id en solitario)
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_technique_owner
    ON technique(owner_id);

-- Registros activos (soft-delete)
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_technique_deleted_at
    ON technique(deleted_at) WHERE deleted_at IS NULL;

-- family añadida en V222 — se usa en filtros de búsqueda
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_technique_family
    ON technique(family);

-- ─── exercise ────────────────────────────────────────────────────────────────
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_exercise_owner
    ON exercise(owner_id);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_exercise_deleted_at
    ON exercise(deleted_at) WHERE deleted_at IS NULL;

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_exercise_category
    ON exercise(category);

-- ─── injury ──────────────────────────────────────────────────────────────────
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_injury_owner
    ON injury(owner_id);

-- status se filtra para mostrar lesiones activas vs. recuperadas
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_injury_owner_status
    ON injury(owner_id, status);

-- ─── competition_log ─────────────────────────────────────────────────────────
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_competition_log_owner_date
    ON competition_log(owner_id, event_date DESC);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_competition_log_deleted_at
    ON competition_log(deleted_at) WHERE deleted_at IS NULL;

-- ─── competition_match ───────────────────────────────────────────────────────
-- FK sin índice: submission_technique_id
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_competition_match_technique
    ON competition_match(submission_technique_id)
    WHERE submission_technique_id IS NOT NULL;

-- ─── study_plan ──────────────────────────────────────────────────────────────
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_study_plan_owner
    ON study_plan(owner_id);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_study_plan_deleted_at
    ON study_plan(deleted_at) WHERE deleted_at IS NULL;

-- ─── study_block ─────────────────────────────────────────────────────────────
-- FK study_plan_id: ya hay ON DELETE CASCADE pero PostgreSQL no crea el índice
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_study_block_plan
    ON study_block(study_plan_id);

-- ─── note ────────────────────────────────────────────────────────────────────
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_note_deleted_at
    ON note(deleted_at) WHERE deleted_at IS NULL;

-- ─── training_session ────────────────────────────────────────────────────────
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_training_session_deleted_at
    ON training_session(deleted_at) WHERE deleted_at IS NULL;

-- ─── training_session_technique ──────────────────────────────────────────────
-- FK technique_id sin índice en la tabla junction
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_tst_technique
    ON training_session_technique(technique_id);

-- ─── system ──────────────────────────────────────────────────────────────────
-- FK anchor_position_id sin índice
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_system_anchor_position
    ON system(anchor_position_id)
    WHERE anchor_position_id IS NOT NULL;

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_system_deleted_at
    ON system(deleted_at) WHERE deleted_at IS NULL;

-- ─── ruleset ─────────────────────────────────────────────────────────────────
-- FK federation_id: la UNIQUE (federation_id, belt, modality, effective_from)
-- cubre consultas que incluyan federation_id como primera columna, pero un
-- índice dedicado acelera lookups simples por federation_id
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_ruleset_federation
    ON ruleset(federation_id);

-- ─── ruleset_technique ───────────────────────────────────────────────────────
-- FK technique_id sin índice en la tabla junction
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_ruleset_technique_technique
    ON ruleset_technique(technique_id);

-- ─── user_profile_federation ─────────────────────────────────────────────────
-- FK federation_id sin índice
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_upf_federation
    ON user_profile_federation(federation_id);
