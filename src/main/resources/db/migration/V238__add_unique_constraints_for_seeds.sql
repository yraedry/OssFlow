-- V238__add_unique_constraints_for_seeds.sql
-- Deduplica ejercicios y añade unique index para que las R__ migrations sean idempotentes.
-- Mantiene el registro con id más bajo (el original) y elimina duplicados posteriores.

DELETE FROM exercise
WHERE id NOT IN (
    SELECT MIN(id)
    FROM exercise
    WHERE deleted_at IS NULL
    GROUP BY owner_id, name
)
AND deleted_at IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ux_exercise_owner_name_active
    ON exercise(owner_id, name) WHERE deleted_at IS NULL;
