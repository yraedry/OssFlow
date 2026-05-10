-- dev-seed.sql
-- Datos de prueba para desarrollo local. NO usar en producción.
-- Ejecutar manualmente: psql -U ossflow -d ossflow -f src/main/resources/db/dev-seed.sql
-- Requiere que las migraciones Flyway ya estén aplicadas (el catálogo debe existir).

-- =====================================================================
-- Usuario de prueba
-- =====================================================================
INSERT INTO user_profile (owner_id, display_name, current_belt, belt_since, academy, preferred_modality, onboarding_completed, created_at, updated_at, version)
VALUES (1, 'Dev User', 'BLUE', '2023-06-01', 'Dev Academy BJJ', 'BOTH', true, NOW(), NOW(), 0)
ON CONFLICT (owner_id) DO NOTHING;

-- =====================================================================
-- Sesiones BJJ (últimas 4 semanas)
-- =====================================================================
INSERT INTO training_session (owner_id, session_date, duration_minutes, intensity, session_type, notes_markdown, created_at, updated_at, version)
VALUES
(1, NOW() - INTERVAL '2 days',  90, 'HIGH',   'BJJ', 'Sparring con cinturones azules. Trabajé guardia De La Riva. Conseguí 3 barridos exitosos.', NOW(), NOW(), 0),
(1, NOW() - INTERVAL '4 days',  60, 'MEDIUM', 'BJJ', 'Drill de cadenas desde half guard. 200 repeticiones cada lado. Muy cansado al final.', NOW(), NOW(), 0),
(1, NOW() - INTERVAL '6 days',  75, 'HIGH',   'BJJ', 'Open mat del sábado. 6 rounds de 6 minutos. Me costó mantener el juego de piernas.', NOW(), NOW(), 0),
(1, NOW() - INTERVAL '9 days',  90, 'HIGH',   'BJJ', 'Clase nocturna. Trabajé pasajes bajo presión. El torreando me está saliendo bien.', NOW(), NOW(), 0),
(1, NOW() - INTERVAL '11 days', 60, 'LOW',    'BJJ', 'Clase técnica: sistema De La Riva completo. Muchos detalles nuevos sobre el berimbolo.', NOW(), NOW(), 0),
(1, NOW() - INTERVAL '13 days', 75, 'HIGH',   'BJJ', 'Sparring competitivo con visitantes. Perdí posición varias veces desde DLR.', NOW(), NOW(), 0),
(1, NOW() - INTERVAL '16 days', 90, 'MEDIUM', 'BJJ', 'Drill de camarones y movimientos de cadera. Fundamental para la defensa.', NOW(), NOW(), 0),
(1, NOW() - INTERVAL '18 days', 60, 'LOW',    'BJJ', 'Clase técnica: leg locks desde single leg X. Primer contacto con inside heel hook.', NOW(), NOW(), 0),
(1, NOW() - INTERVAL '20 days', 75, 'HIGH',   'BJJ', 'Sparring libre. Foco en no perder posición. Aguanté bien desde bottom side control.', NOW(), NOW(), 0),
(1, NOW() - INTERVAL '23 days', 90, 'MEDIUM', 'BJJ', 'Open mat. Experimenté con rubber guard. Muy difícil sin flexibilidad suficiente.', NOW(), NOW(), 0);

-- Técnicas trabajadas en sesiones
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count, notes_markdown)
SELECT ts.id, t.id, 20, 'Trabajado en clase'
FROM training_session ts
CROSS JOIN technique t
WHERE ts.owner_id = 1
  AND t.name IN ('Armbar', 'Triangle Choke', 'Kimura', 'Rear Naked Choke', 'Guillotine')
  AND t.owner_id = 1
ON CONFLICT DO NOTHING;

-- =====================================================================
-- Sesiones físicas
-- =====================================================================
INSERT INTO physical_session (owner_id, session_date, duration_minutes, session_type, title, notes, created_at, updated_at, version)
VALUES
(1, NOW() - INTERVAL '3 days',  45, 'STRENGTH',    'Fuerza tren superior',           'Press banca, dominadas, remo. Progresé en dominadas: 4x10.', NOW(), NOW(), 0),
(1, NOW() - INTERVAL '7 days',  30, 'CARDIO',      'Carrera continua',               'Carrera 5km + intervalos finales. Ritmo 5:20/km.', NOW(), NOW(), 0),
(1, NOW() - INTERVAL '10 days', 45, 'STRENGTH',    'Circuito funcional BJJ',         'Circuito de fuerza funcional: kettlebell + TRX.', NOW(), NOW(), 0),
(1, NOW() - INTERVAL '14 days', 60, 'MOBILITY',    'Movilidad completa',             'Sesión completa: cadera, hombros, columna torácica.', NOW(), NOW(), 0),
(1, NOW() - INTERVAL '17 days', 30, 'FLEXIBILITY', 'Estiramientos post-entreno',     'Estiramientos estáticos 30 seg cada posición.', NOW(), NOW(), 0),
(1, NOW() - INTERVAL '21 days', 45, 'STRENGTH',    'Fuerza tren inferior',           'Sentadillas, peso muerto, hip thrust. PR en sentadilla: 90kg.', NOW(), NOW(), 0),
(1, NOW() - INTERVAL '24 days', 20, 'CARDIO',      'HIIT corto',                     '10 rondas: 20s esfuerzo / 10s descanso en remo.', NOW(), NOW(), 0);

-- Ejercicios en sesiones físicas
INSERT INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps, duration_seconds, notes)
SELECT ps.id, e.id,
       CASE e.category WHEN 'STRENGTH' THEN 4 WHEN 'CARDIO' THEN 3 ELSE 3 END,
       CASE e.category WHEN 'STRENGTH' THEN 10 WHEN 'CARDIO' THEN 20 ELSE 15 END,
       NULL, NULL
FROM physical_session ps
JOIN exercise e ON e.name IN ('Push-Up', 'Pull-Up', 'Squat', 'Hip Escape Drill', 'Couch Stretch')
  AND e.owner_id = 1
WHERE ps.owner_id = 1
ON CONFLICT DO NOTHING;

-- =====================================================================
-- Plan de estudio
-- =====================================================================
INSERT INTO study_plan (owner_id, title, goal_markdown, start_date, end_date, status, created_at, updated_at, version)
VALUES (1, 'Plan De La Riva 8 semanas',
        '## Objetivo\nDominar el sistema De La Riva completo.\n\n## Metas\n- Ejecutar barrido DLR con 70% efectividad\n- Conectar berimbolo en sparring\n- Transición fluida a single leg X',
        CURRENT_DATE, CURRENT_DATE + INTERVAL '56 days', 'ACTIVE', NOW(), NOW(), 0)
ON CONFLICT DO NOTHING;
