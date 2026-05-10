-- study_plan: start_date and end_date should be optional (DRAFT plans may not have dates)
ALTER TABLE study_plan ALTER COLUMN start_date DROP NOT NULL;
ALTER TABLE study_plan ALTER COLUMN end_date DROP NOT NULL;
