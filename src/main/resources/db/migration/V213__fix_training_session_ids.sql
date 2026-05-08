-- Fix: la columna id de training_session quedó sin tipo en SQLite y los ids son NULL
-- Copiar el rowid a la columna id para las filas existentes
UPDATE training_session SET id = rowid WHERE id IS NULL;
