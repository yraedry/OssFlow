-- V235__fix_injury_date_columns.sql
-- La columna recovered_on (y started_on) fueron creadas como varchar(20)
-- pero la entidad InjuryEntity las mapea como LocalDate (tipo DATE).
-- Con ddl-auto=validate el backend no arranca si los tipos no coinciden.
-- Convertimos ambas columnas a DATE usando USING con cast seguro.

ALTER TABLE injury
  ALTER COLUMN recovered_on TYPE date
    USING CASE
      WHEN recovered_on IS NULL OR trim(recovered_on) = '' THEN NULL
      ELSE recovered_on::date
    END;

ALTER TABLE injury
  ALTER COLUMN started_on TYPE date
    USING CASE
      WHEN started_on IS NULL OR trim(started_on) = '' THEN NULL
      ELSE started_on::date
    END;
