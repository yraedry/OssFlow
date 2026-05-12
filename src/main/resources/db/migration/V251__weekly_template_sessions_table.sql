-- Nueva tabla relacional para sesiones de la plantilla semanal
CREATE TABLE weekly_template_session (
    id          BIGSERIAL PRIMARY KEY,
    template_id BIGINT NOT NULL REFERENCES weekly_template(id) ON DELETE CASCADE,
    day_of_week VARCHAR(9) NOT NULL,
    session_type VARCHAR(15) NOT NULL,
    time        VARCHAR(5),  -- "HH:mm" opcional
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_wts_template_id ON weekly_template_session(template_id);

-- Migrar datos existentes del JSON a la nueva tabla
DO $$
DECLARE
    rec RECORD;
    day_elem JSONB;
    slot JSONB;
    type_map JSONB := '{
        "bjj": "BJJ",
        "strength": "STRENGTH",
        "cardio": "CARDIO",
        "mobility": "MOBILITY",
        "flexibility": "FLEXIBILITY"
    }';
    field TEXT;
    fields TEXT[] := ARRAY['bjj','strength','cardio','mobility','flexibility'];
BEGIN
    FOR rec IN SELECT id, days_json FROM weekly_template WHERE days_json IS NOT NULL AND days_json != '[]' LOOP
        FOR day_elem IN SELECT * FROM jsonb_array_elements(rec.days_json::jsonb) LOOP
            -- Formato antiguo: {dayOfWeek, bjj, strength, cardio, mobility, flexibility}
            IF day_elem ? 'bjj' THEN
                FOREACH field IN ARRAY fields LOOP
                    IF (day_elem ->> field)::boolean THEN
                        INSERT INTO weekly_template_session (template_id, day_of_week, session_type)
                        VALUES (rec.id, day_elem ->> 'dayOfWeek', type_map ->> field);
                    END IF;
                END LOOP;
            -- Formato nuevo: {dayOfWeek, sessions: [{type, time}]}
            ELSIF day_elem ? 'sessions' THEN
                FOR slot IN SELECT * FROM jsonb_array_elements(day_elem -> 'sessions') LOOP
                    INSERT INTO weekly_template_session (template_id, day_of_week, session_type, time)
                    VALUES (rec.id, day_elem ->> 'dayOfWeek', slot ->> 'type', slot ->> 'time');
                END LOOP;
            END IF;
        END LOOP;
    END LOOP;
END $$;

-- Eliminar columna JSON ya migrada
ALTER TABLE weekly_template DROP COLUMN IF EXISTS days_json;
