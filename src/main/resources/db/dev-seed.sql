-- dev-seed.sql
-- Datos de prueba para desarrollo local. NO usar en producción.
-- Ejecutar manualmente: psql -U ossflow -d ossflow -f src/main/resources/db/dev-seed.sql
-- Requiere que las migraciones Flyway ya estén aplicadas (el catálogo debe existir).

-- =====================================================================
-- Usuario de prueba
-- =====================================================================
INSERT INTO user_profile (id, username, email, belt, stripe_count, weight_kg, academy, bio, visibility, created_at, updated_at, version)
VALUES (1, 'dev_user', 'dev@ossflow.es', 'BLUE', 2, 75.0, 'Dev Academy', 'Usuario de prueba para desarrollo', 'PRIVATE', NOW(), NOW(), 0)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================
-- Sesiones BJJ (últimas 4 semanas)
-- =====================================================================
INSERT INTO training_session (owner_id, session_date, duration_minutes, session_type, notes, visibility, created_at, updated_at, version)
VALUES
(1, NOW() - INTERVAL '2 days',  90, 'SPARRING',    'Sparring con cinturones azules. Trabajé guardia De La Riva.', 'PRIVATE', NOW(), NOW(), 0),
(1, NOW() - INTERVAL '4 days',  60, 'DRILLING',    'Drill de cadenas desde half guard. 200 repeticiones cada lado.', 'PRIVATE', NOW(), NOW(), 0),
(1, NOW() - INTERVAL '6 days',  75, 'OPEN_MAT',    'Open mat del sábado. 6 rounds de 6 minutos.', 'PRIVATE', NOW(), NOW(), 0),
(1, NOW() - INTERVAL '9 days',  90, 'SPARRING',    'Clase nocturna. Trabajé pasajes bajo presión.', 'PRIVATE', NOW(), NOW(), 0),
(1, NOW() - INTERVAL '11 days', 60, 'TECHNIQUE',   'Clase técnica: sistema De La Riva completo.', 'PRIVATE', NOW(), NOW(), 0),
(1, NOW() - INTERVAL '13 days', 75, 'SPARRING',    'Sparring competitivo con visitantes del gimnasio.', 'PRIVATE', NOW(), NOW(), 0),
(1, NOW() - INTERVAL '16 days', 90, 'DRILLING',    'Drill de camarones y movimientos de cadera.', 'PRIVATE', NOW(), NOW(), 0),
(1, NOW() - INTERVAL '18 days', 60, 'TECHNIQUE',   'Clase técnica: leg locks desde single leg X.', 'PRIVATE', NOW(), NOW(), 0),
(1, NOW() - INTERVAL '20 days', 75, 'SPARRING',    'Sparring libre. Foco en no perder posición.', 'PRIVATE', NOW(), NOW(), 0),
(1, NOW() - INTERVAL '23 days', 90, 'OPEN_MAT',    'Open mat. Experimenté con rubber guard.', 'PRIVATE', NOW(), NOW(), 0);

-- Técnicas trabajadas en sesiones (usando las técnicas del catálogo)
INSERT INTO training_session_technique (training_session_id, technique_id, note, created_at)
SELECT ts.id, t.id, 'Trabajado en clase', NOW()
FROM training_session ts
CROSS JOIN technique t
WHERE ts.owner_id = 1
  AND t.name IN ('De La Riva Sweep', 'Berimbolo', 'Single Leg X Entry', 'Heel Hook Entry', 'Kimura')
  AND t.owner_id = 1
ON CONFLICT DO NOTHING;

-- =====================================================================
-- Sesiones físicas
-- =====================================================================
INSERT INTO physical_session (owner_id, session_date, duration_minutes, session_type, notes, visibility, created_at, updated_at, version)
VALUES
(1, NOW() - INTERVAL '3 days',  45, 'STRENGTH',    'Fuerza: press banca, dominadas, sentadillas.', 'PRIVATE', NOW(), NOW(), 0),
(1, NOW() - INTERVAL '7 days',  30, 'CARDIO',      'Carrera continua 5km + intervalos finales.', 'PRIVATE', NOW(), NOW(), 0),
(1, NOW() - INTERVAL '10 days', 45, 'STRENGTH',    'Circuito de fuerza funcional BJJ.', 'PRIVATE', NOW(), NOW(), 0),
(1, NOW() - INTERVAL '14 days', 60, 'MOBILITY',    'Sesión completa de movilidad: cadera, hombros, columna.', 'PRIVATE', NOW(), NOW(), 0),
(1, NOW() - INTERVAL '17 days', 30, 'FLEXIBILITY', 'Estiramientos post-entrenamiento.', 'PRIVATE', NOW(), NOW(), 0);

-- Ejercicios en sesiones físicas
INSERT INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps, weight_kg, duration_seconds, notes, created_at)
SELECT ps.id, e.id,
       CASE e.category WHEN 'STRENGTH' THEN 4 WHEN 'CARDIO' THEN 3 ELSE 3 END,
       CASE e.category WHEN 'STRENGTH' THEN 10 WHEN 'CARDIO' THEN 20 ELSE 15 END,
       CASE e.category WHEN 'STRENGTH' THEN 0 ELSE NULL END,
       NULL, NULL, NOW()
FROM physical_session ps
JOIN exercise e ON e.name IN ('Push-Up', 'Pull-Up', 'Squat', 'Hip Escape Drill', 'Couch Stretch')
  AND e.owner_id = 1
WHERE ps.owner_id = 1
ON CONFLICT DO NOTHING;

-- =====================================================================
-- Plan de estudio
-- =====================================================================
INSERT INTO study_plan (owner_id, name, description, visibility, created_at, updated_at, version)
VALUES (1, 'Plan De La Riva 8 semanas', 'Dominar el sistema De La Riva completo: barridos, berimbolo y transiciones a leg locks.', 'PRIVATE', NOW(), NOW(), 0)
ON CONFLICT DO NOTHING;
