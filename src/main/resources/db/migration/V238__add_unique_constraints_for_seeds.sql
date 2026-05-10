-- V238__add_unique_constraints_for_seeds.sql
-- Añade unique indexes necesarios para que las R__ migrations sean idempotentes.

CREATE UNIQUE INDEX IF NOT EXISTS ux_exercise_owner_name_active
    ON exercise(owner_id, name) WHERE deleted_at IS NULL;
