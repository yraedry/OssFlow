-- study_block.start_date y end_date pasan a nullable para permitir
-- crear bloques sin fechas desde el frontend (las fechas son opcionales).
ALTER TABLE study_block ALTER COLUMN start_date DROP NOT NULL;
ALTER TABLE study_block ALTER COLUMN end_date DROP NOT NULL;
