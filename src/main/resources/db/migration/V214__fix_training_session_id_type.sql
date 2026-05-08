-- V214: no-op en PostgreSQL.
-- Originalmente recreaba la tabla en SQLite para corregir el tipo del ID.
-- En PostgreSQL la tabla training_session ya tiene id BIGSERIAL desde V4.
SELECT 1;
