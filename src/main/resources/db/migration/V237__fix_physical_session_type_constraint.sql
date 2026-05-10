-- Add MOBILITY to physical_session_type check constraint
ALTER TABLE physical_session DROP CONSTRAINT IF EXISTS physical_session_session_type_check;
ALTER TABLE physical_session ADD CONSTRAINT physical_session_session_type_check
    CHECK (session_type IN ('STRENGTH','CARDIO','FLEXIBILITY','MOBILITY','HIIT','OTHER'));
