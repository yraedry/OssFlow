-- V224__seed_demo_data.sql
-- Batería de datos de demostración: 90 días de sesiones BJJ y físicas con técnicas y ejercicios.
-- Todos los datos son para owner_id=1.
-- SQLite: usar date('now', '-N days') y datetime('now') — NO CURRENT_DATE ni CURRENT_TIMESTAMP aritméticos.

-- ============================================================
-- SESIONES BJJ (últimos 90 días, ~3-4 por semana)
-- ============================================================

INSERT INTO training_session (owner_id, session_date, duration_minutes, location, intensity, notes_markdown, created_at, updated_at, version)
VALUES
(1, date('now', '-90 days'), 90, 'Gimnasio',    'HARD',       'Sparring intenso. Trabajé mucho la guardia cerrada.',          datetime('now'), datetime('now'), 0),
(1, date('now', '-88 days'), 75, 'Gimnasio',    'MODERATE',   'Clase técnica: triángulos y armbars encadenados.',             datetime('now'), datetime('now'), 0),
(1, date('now', '-86 days'), 60, 'Gimnasio',    'LIGHT',      'Técnica. Enfocado en pasadas de guardia.',                     datetime('now'), datetime('now'), 0),
(1, date('now', '-83 days'), 90, 'Gimnasio',    'HARD',       'Competición interna. 4 luchas.',                               datetime('now'), datetime('now'), 0),
(1, date('now', '-81 days'), 75, 'Gimnasio',    'MODERATE',   'Clase: leg locks básicos y defensa.',                          datetime('now'), datetime('now'), 0),
(1, date('now', '-79 days'), 60, 'Casa',        'LIGHT',      'Solo drilling. Movimiento de caderas y shrimping.',            datetime('now'), datetime('now'), 0),
(1, date('now', '-76 days'), 90, 'Gimnasio',    'HARD',       'Sparring. Trabajé tomas de espalda.',                          datetime('now'), datetime('now'), 0),
(1, date('now', '-74 days'), 75, 'Gimnasio',    'MODERATE',   'Clase: barridas desde half guard.',                            datetime('now'), datetime('now'), 0),
(1, date('now', '-72 days'), 90, 'Gimnasio',    'HARD',       'Open mat. Mucho juego con guillotinas.',                       datetime('now'), datetime('now'), 0),
(1, date('now', '-71 days'), 60, 'Gimnasio',    'LIGHT',      'Técnica: derribos y takedowns.',                               datetime('now'), datetime('now'), 0),
(1, date('now', '-69 days'), 90, 'Gimnasio',    'HARD',       'Sparring duro. Shoulder locks encadenados.',                   datetime('now'), datetime('now'), 0),
(1, date('now', '-67 days'), 75, 'Gimnasio',    'MODERATE',   'Clase: escapadas de mount y side control.',                    datetime('now'), datetime('now'), 0),
(1, date('now', '-65 days'), 60, 'Gimnasio',    'LIGHT',      'Técnica: chokes desde guardia.',                               datetime('now'), datetime('now'), 0),
(1, date('now', '-62 days'), 90, 'Gimnasio',    'HARD',       'Open mat. Triángulos y omoplatas.',                            datetime('now'), datetime('now'), 0),
(1, date('now', '-60 days'), 75, 'Gimnasio',    'MODERATE',   'Clase: pasadas de guardia spider y lasso.',                    datetime('now'), datetime('now'), 0),
(1, date('now', '-58 days'), 90, 'Gimnasio',    'HARD',       'Sparring. Mucho back take desde turtle.',                      datetime('now'), datetime('now'), 0),
(1, date('now', '-55 days'), 90, 'Gimnasio',    'HARD',       'Competición externa. 3 luchas ganadas.',                       datetime('now'), datetime('now'), 0),
(1, date('now', '-53 days'), 75, 'Gimnasio',    'MODERATE',   'Clase técnica: RNC y bow and arrow.',                          datetime('now'), datetime('now'), 0),
(1, date('now', '-51 days'), 60, 'Casa',        'LIGHT',      'Drilling: armbars desde guardia.',                             datetime('now'), datetime('now'), 0),
(1, date('now', '-48 days'), 90, 'Gimnasio',    'HARD',       'Sparring intenso. Leg locks ataques y defensas.',              datetime('now'), datetime('now'), 0),
(1, date('now', '-46 days'), 75, 'Gimnasio',    'MODERATE',   'Clase: barridas desde X-guard.',                               datetime('now'), datetime('now'), 0),
(1, date('now', '-44 days'), 90, 'Gimnasio',    'HARD',       'Open mat. Juego de guardia abierta.',                          datetime('now'), datetime('now'), 0),
(1, date('now', '-43 days'), 60, 'Gimnasio',    'LIGHT',      'Técnica: double leg y penetration step.',                      datetime('now'), datetime('now'), 0),
(1, date('now', '-41 days'), 90, 'Gimnasio',    'HARD',       'Sparring. Kimuras y americanas encadenadas.',                  datetime('now'), datetime('now'), 0),
(1, date('now', '-39 days'), 75, 'Gimnasio',    'MODERATE',   'Clase: D''Arce y Anaconda choke.',                             datetime('now'), datetime('now'), 0),
(1, date('now', '-37 days'), 60, 'Gimnasio',    'LIGHT',      'Técnica: Omoplata y baratoplata.',                             datetime('now'), datetime('now'), 0),
(1, date('now', '-34 days'), 90, 'Gimnasio',    'HARD',       'Open mat. Mucho trabajo en el suelo.',                         datetime('now'), datetime('now'), 0),
(1, date('now', '-32 days'), 75, 'Gimnasio',    'MODERATE',   'Clase: tomas de espalda desde guardia.',                       datetime('now'), datetime('now'), 0),
(1, date('now', '-30 days'), 90, 'Gimnasio',    'HARD',       'Sparring duro. Triángulos desde guardia invertida.',           datetime('now'), datetime('now'), 0),
(1, date('now', '-27 days'), 90, 'Gimnasio',    'HARD',       'Open mat. Leg locks y kneebars.',                              datetime('now'), datetime('now'), 0),
(1, date('now', '-25 days'), 75, 'Gimnasio',    'MODERATE',   'Clase: pasadas pressure y toreando.',                          datetime('now'), datetime('now'), 0),
(1, date('now', '-23 days'), 60, 'Gimnasio',    'LIGHT',      'Técnica: guillotinas arm-in y high elbow.',                    datetime('now'), datetime('now'), 0),
(1, date('now', '-20 days'), 90, 'Gimnasio',    'HARD',       'Sparring. Mucho trabajo de top game.',                         datetime('now'), datetime('now'), 0),
(1, date('now', '-18 days'), 75, 'Gimnasio',    'MODERATE',   'Clase: escapadas de guardia cerrada.',                         datetime('now'), datetime('now'), 0),
(1, date('now', '-16 days'), 90, 'Gimnasio',    'HARD',       'Open mat. Back mount ataques.',                                datetime('now'), datetime('now'), 0),
(1, date('now', '-13 days'), 90, 'Gimnasio',    'HARD',       'Sparring intenso. Mejor ritmo en guard passing.',              datetime('now'), datetime('now'), 0),
(1, date('now', '-11 days'), 75, 'Gimnasio',    'MODERATE',   'Clase: single leg y double leg desde clinch.',                 datetime('now'), datetime('now'), 0),
(1, date('now', '-9 days'),  60, 'Gimnasio',    'LIGHT',      'Técnica: wristlocks y pequeñas llaves.',                       datetime('now'), datetime('now'), 0),
(1, date('now', '-6 days'),  90, 'Gimnasio',    'HARD',       'Open mat. Fui de competición. Buen día.',                      datetime('now'), datetime('now'), 0),
(1, date('now', '-4 days'),  75, 'Gimnasio',    'MODERATE',   'Clase: triángulos desde X-guard.',                             datetime('now'), datetime('now'), 0),
(1, date('now', '-2 days'),  90, 'Gimnasio',    'HARD',       'Sparring. Armbar desde guardia muy fluido hoy.',               datetime('now'), datetime('now'), 0);

-- ============================================================
-- TÉCNICAS TRABAJADAS EN SESIONES BJJ
-- ============================================================

-- Sesión 1 (-90)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count, notes_markdown)
SELECT ts.id, t.id, 20, 'Drilling básico'
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-90 days')
  AND t.owner_id=1 AND t.name='Armbar' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-90 days')
  AND t.owner_id=1 AND t.name='Triangle Choke' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 12
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-90 days')
  AND t.owner_id=1 AND t.name='Kimura' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 2 (-88)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 25
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-88 days')
  AND t.owner_id=1 AND t.name='Triangle Choke' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-88 days')
  AND t.owner_id=1 AND t.name='Armbar' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 3 (-86)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 18
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-86 days')
  AND t.owner_id=1 AND t.name='Torreando Pass' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-86 days')
  AND t.owner_id=1 AND t.name='Knee Slice Pass' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 4 (-83 competición)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 10
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-83 days')
  AND t.owner_id=1 AND t.name='Rear Naked Choke' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 8
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-83 days')
  AND t.owner_id=1 AND t.name='Armbar' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 6
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-83 days')
  AND t.owner_id=1 AND t.name='Double Leg Takedown' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 5 (-81 leg locks)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 22
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-81 days')
  AND t.owner_id=1 AND t.name='Heel Hook' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 18
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-81 days')
  AND t.owner_id=1 AND t.name='Kneebar' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 6 (-76 tomas de espalda)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-76 days')
  AND t.owner_id=1 AND t.name='Rear Naked Choke' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-76 days')
  AND t.owner_id=1 AND t.name='Body Triangle to Back Take' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 7 (-74 barridas)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-74 days')
  AND t.owner_id=1 AND t.name='Scissor Sweep' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 18
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-74 days')
  AND t.owner_id=1 AND t.name='Hip Bump Sweep' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-74 days')
  AND t.owner_id=1 AND t.name='Flower Sweep' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 8 (-72 guillotinas)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-72 days')
  AND t.owner_id=1 AND t.name='Guillotine Choke' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 12
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-72 days')
  AND t.owner_id=1 AND t.name='Kimura' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 9 (-71 derribos)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-71 days')
  AND t.owner_id=1 AND t.name='Double Leg Takedown' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-71 days')
  AND t.owner_id=1 AND t.name='Single Leg Takedown' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 10 (-69 shoulder locks)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 18
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-69 days')
  AND t.owner_id=1 AND t.name='Kimura' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 18
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-69 days')
  AND t.owner_id=1 AND t.name='Americana' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 12
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-69 days')
  AND t.owner_id=1 AND t.name='Omoplata' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 11 (-67 escapadas)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 25
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-67 days')
  AND t.owner_id=1 AND t.name='Mount Escape (Elbow-Knee)' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-67 days')
  AND t.owner_id=1 AND t.name='Side Control Escape (Shrimp)' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 12 (-65 chokes desde guardia)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-65 days')
  AND t.owner_id=1 AND t.name='Triangle Choke' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-65 days')
  AND t.owner_id=1 AND t.name='Guillotine Choke' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 13 (-62 triángulos y omoplatas)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-62 days')
  AND t.owner_id=1 AND t.name='Triangle Choke' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-62 days')
  AND t.owner_id=1 AND t.name='Omoplata' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 14 (-60 pasadas)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-60 days')
  AND t.owner_id=1 AND t.name='Torreando Pass' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 18
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-60 days')
  AND t.owner_id=1 AND t.name='Over-Under Pass' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 15 (-58 back takes)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-58 days')
  AND t.owner_id=1 AND t.name='Seat Belt Grip Back Take' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-58 days')
  AND t.owner_id=1 AND t.name='Rear Naked Choke' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 16 (-55 competición)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 8
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-55 days')
  AND t.owner_id=1 AND t.name='Armbar' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 6
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-55 days')
  AND t.owner_id=1 AND t.name='Double Leg Takedown' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 17 (-53 RNC y bow and arrow)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-53 days')
  AND t.owner_id=1 AND t.name='Rear Naked Choke' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-53 days')
  AND t.owner_id=1 AND t.name='Bow and Arrow Choke' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 18 (-51 armbars drilling)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 30
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-51 days')
  AND t.owner_id=1 AND t.name='Armbar' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 19 (-48 leg locks)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-48 days')
  AND t.owner_id=1 AND t.name='Heel Hook' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-48 days')
  AND t.owner_id=1 AND t.name='Kneebar' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 10
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-48 days')
  AND t.owner_id=1 AND t.name='Toe Hold' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 20 (-46 barridas X-guard)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 18
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-46 days')
  AND t.owner_id=1 AND t.name='X-Guard Sweep' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-46 days')
  AND t.owner_id=1 AND t.name='Scissor Sweep' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 21 (-44)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-44 days')
  AND t.owner_id=1 AND t.name='Triangle Choke' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 12
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-44 days')
  AND t.owner_id=1 AND t.name='Armbar' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 22 (-43 derribos)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-43 days')
  AND t.owner_id=1 AND t.name='Double Leg Takedown' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-43 days')
  AND t.owner_id=1 AND t.name='Single Leg Takedown' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 23 (-41 kimuras y americanas)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-41 days')
  AND t.owner_id=1 AND t.name='Kimura' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 18
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-41 days')
  AND t.owner_id=1 AND t.name='Americana' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 24 (-39 D'Arce y Anaconda)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-39 days')
  AND t.owner_id=1 AND t.name='D''Arce Choke' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-39 days')
  AND t.owner_id=1 AND t.name='Anaconda Choke' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 25 (-37 Omoplata)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 18
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-37 days')
  AND t.owner_id=1 AND t.name='Omoplata' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 12
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-37 days')
  AND t.owner_id=1 AND t.name='Kimura' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 26 (-34)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-34 days')
  AND t.owner_id=1 AND t.name='Rear Naked Choke' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 12
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-34 days')
  AND t.owner_id=1 AND t.name='Bow and Arrow Choke' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 10
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-34 days')
  AND t.owner_id=1 AND t.name='Body Triangle to Back Take' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 27 (-32 back takes)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 18
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-32 days')
  AND t.owner_id=1 AND t.name='Seat Belt Grip Back Take' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-32 days')
  AND t.owner_id=1 AND t.name='Rear Naked Choke' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 28 (-30 triángulos)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 25
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-30 days')
  AND t.owner_id=1 AND t.name='Triangle Choke' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 12
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-30 days')
  AND t.owner_id=1 AND t.name='Armbar' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 29 (-27 leg locks)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 18
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-27 days')
  AND t.owner_id=1 AND t.name='Heel Hook' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 12
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-27 days')
  AND t.owner_id=1 AND t.name='Kneebar' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 30 (-25 pasadas)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-25 days')
  AND t.owner_id=1 AND t.name='Torreando Pass' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 18
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-25 days')
  AND t.owner_id=1 AND t.name='Over-Under Pass' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 31 (-23 guillotinas)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 22
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-23 days')
  AND t.owner_id=1 AND t.name='Guillotine Choke' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 32 (-20)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-20 days')
  AND t.owner_id=1 AND t.name='Knee Slice Pass' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 12
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-20 days')
  AND t.owner_id=1 AND t.name='Torreando Pass' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 33 (-18 escapadas)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 25
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-18 days')
  AND t.owner_id=1 AND t.name='Mount Escape (Elbow-Knee)' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-18 days')
  AND t.owner_id=1 AND t.name='Side Control Escape (Shrimp)' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 34 (-16 back mount)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-16 days')
  AND t.owner_id=1 AND t.name='Rear Naked Choke' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-16 days')
  AND t.owner_id=1 AND t.name='Seat Belt Grip Back Take' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 35 (-13)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 18
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-13 days')
  AND t.owner_id=1 AND t.name='Over-Under Pass' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-13 days')
  AND t.owner_id=1 AND t.name='Armbar' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 36 (-11 derribos)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 22
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-11 days')
  AND t.owner_id=1 AND t.name='Single Leg Takedown' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 18
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-11 days')
  AND t.owner_id=1 AND t.name='Double Leg Takedown' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 37 (-9 wristlocks / llaves de muñeca)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-9 days')
  AND t.owner_id=1 AND t.name='Kimura' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 12
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-9 days')
  AND t.owner_id=1 AND t.name='Americana' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 38 (-6)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-6 days')
  AND t.owner_id=1 AND t.name='Triangle Choke' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 12
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-6 days')
  AND t.owner_id=1 AND t.name='Guillotine Choke' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 39 (-4 triángulos X-guard)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 22
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-4 days')
  AND t.owner_id=1 AND t.name='Triangle Choke' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-4 days')
  AND t.owner_id=1 AND t.name='X-Guard Sweep' AND t.deleted_at IS NULL LIMIT 1;

-- Sesión 40 (-2 armbar desde guardia)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 25
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-2 days')
  AND t.owner_id=1 AND t.name='Armbar' AND t.deleted_at IS NULL LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = date('now', '-2 days')
  AND t.owner_id=1 AND t.name='Triangle Choke' AND t.deleted_at IS NULL LIMIT 1;

-- ============================================================
-- SESIONES FÍSICAS (últimos 90 días, ~2-3 por semana)
-- ============================================================

INSERT INTO physical_session (owner_id, session_date, session_type, title, duration_minutes, notes, created_at, updated_at, version)
VALUES
-- Fuerza (STRENGTH)
(1, date('now', '-89 days'), 'STRENGTH',    'Fuerza tren superior', 60, 'Pull-ups, dips, press. Buena sesión.',             datetime('now'), datetime('now'), 0),
(1, date('now', '-85 days'), 'STRENGTH',    'Fuerza tren inferior', 55, 'Squats, deadlifts, hip thrust.',                  datetime('now'), datetime('now'), 0),
(1, date('now', '-80 days'), 'STRENGTH',    'Fuerza compuesta',     65, 'Bench press, barbell row, overhead press.',       datetime('now'), datetime('now'), 0),
(1, date('now', '-75 days'), 'STRENGTH',    'Fuerza + core',        60, 'Pull-ups, hollow body, L-sit.',                   datetime('now'), datetime('now'), 0),
(1, date('now', '-70 days'), 'STRENGTH',    'Pressing day',         50, 'Push-ups variantes, dips weighted.',              datetime('now'), datetime('now'), 0),
(1, date('now', '-64 days'), 'STRENGTH',    'Pulling day',          55, 'Pull-up negatives, Barbell row, face pulls.',     datetime('now'), datetime('now'), 0),
(1, date('now', '-57 days'), 'STRENGTH',    'Pierna + glúteo',      60, 'Bulgarian split squat, hip thrust, RDL.',         datetime('now'), datetime('now'), 0),
(1, date('now', '-50 days'), 'STRENGTH',    'Fuerza total',         65, 'Grandes patrones de movimiento.',                 datetime('now'), datetime('now'), 0),
(1, date('now', '-42 days'), 'STRENGTH',    'Fuerza tren superior', 60, 'Progresión: más peso que hace 3 semanas.',        datetime('now'), datetime('now'), 0),
(1, date('now', '-35 days'), 'STRENGTH',    'Fuerza + accesorios',  55, 'Compuestos + trabajo unilateral.',                datetime('now'), datetime('now'), 0),
(1, date('now', '-28 days'), 'STRENGTH',    'Fuerza total',         60, 'PR en pull-ups: 12 reps limpias.',                datetime('now'), datetime('now'), 0),
(1, date('now', '-21 days'), 'STRENGTH',    'Tren inferior',        55, 'Deadlift + sentadillas búlgaras.',                datetime('now'), datetime('now'), 0),
(1, date('now', '-14 days'), 'STRENGTH',    'Pressing + core',      60, 'Fondos, pino, planche lean.',                     datetime('now'), datetime('now'), 0),
(1, date('now', '-7 days'),  'STRENGTH',    'Fuerza tren superior', 55, 'Semana de descarga. Menor volumen.',               datetime('now'), datetime('now'), 0),
-- Cardio (CARDIO)
(1, date('now', '-87 days'), 'CARDIO',      'Running 5km',          35, '5km en 25 min. Buen ritmo aeróbico.',             datetime('now'), datetime('now'), 0),
(1, date('now', '-82 days'), 'CARDIO',      'Cycling 40min',        45, 'Bici estática. Zona 2 aeróbico.',                 datetime('now'), datetime('now'), 0),
(1, date('now', '-77 days'), 'CARDIO',      'Running 6km',          40, '6km. Trabajé el ritmo.',                          datetime('now'), datetime('now'), 0),
(1, date('now', '-66 days'), 'CARDIO',      'Jump rope + running',  40, 'Comba 15min + carrera 20min.',                    datetime('now'), datetime('now'), 0),
(1, date('now', '-56 days'), 'CARDIO',      'Running 7km',          45, 'PR distancia. 7km seguidos.',                     datetime('now'), datetime('now'), 0),
(1, date('now', '-45 days'), 'CARDIO',      'Assault bike 20min',   25, 'Cardio de alta intensidad con bici.',             datetime('now'), datetime('now'), 0),
(1, date('now', '-33 days'), 'CARDIO',      'Running 6km',          40, 'Ritmo cómodo. Zona 2.',                           datetime('now'), datetime('now'), 0),
(1, date('now', '-22 days'), 'CARDIO',      'Running 8km',          50, '8km. Mejor marca de la temporada.',               datetime('now'), datetime('now'), 0),
(1, date('now', '-10 days'), 'CARDIO',      'Cycling 45min',        50, 'Recuperación activa.',                            datetime('now'), datetime('now'), 0),
(1, date('now', '-3 days'),  'CARDIO',      'Running 5km',          35, 'Última sesión antes del entrenamiento.',          datetime('now'), datetime('now'), 0),
-- Flexibilidad (FLEXIBILITY)
(1, date('now', '-84 days'), 'FLEXIBILITY', 'Movilidad de cadera',  40, 'Hip flexor, piriforme, aductores.',               datetime('now'), datetime('now'), 0),
(1, date('now', '-78 days'), 'FLEXIBILITY', 'Estiramiento global',  35, 'Cadena posterior, hombros, cuello.',              datetime('now'), datetime('now'), 0),
(1, date('now', '-63 days'), 'FLEXIBILITY', 'Yoga flow',            45, 'Clase de yoga. Mucho trabajo de cadera.',         datetime('now'), datetime('now'), 0),
(1, date('now', '-47 days'), 'FLEXIBILITY', 'Movilidad articular',  35, 'Hombros, caderas, tobillo, muñecas.',             datetime('now'), datetime('now'), 0),
(1, date('now', '-29 days'), 'FLEXIBILITY', 'Deep stretching',      40, 'Estiramiento profundo. Splits progresión.',       datetime('now'), datetime('now'), 0),
(1, date('now', '-15 days'), 'FLEXIBILITY', 'Yoga + movilidad',     45, 'Combinado yoga y trabajo de movilidad.',          datetime('now'), datetime('now'), 0),
-- HIIT (HIIT)
(1, date('now', '-73 days'), 'HIIT',        'Kettlebell circuit',   35, 'Swings, goblet squat, snatches. 4 rondas.',       datetime('now'), datetime('now'), 0),
(1, date('now', '-54 days'), 'HIIT',        'Battle ropes + burpees', 30, 'Intervalos 40/20. Muy exigente.',               datetime('now'), datetime('now'), 0),
(1, date('now', '-38 days'), 'HIIT',        'Box jumps + sprints',  30, 'Potencia explosiva. 6 series.',                   datetime('now'), datetime('now'), 0),
(1, date('now', '-19 days'), 'HIIT',        'WOD crossfit style',   35, 'AMRAP 20min: burpees, wall balls, pull-ups.',     datetime('now'), datetime('now'), 0),
(1, date('now', '-5 days'),  'HIIT',        'Kettlebell + jumps',   30, 'Explosividad. Swings y box jumps.',               datetime('now'), datetime('now'), 0);

-- ============================================================
-- EJERCICIOS TRABAJADOS EN SESIONES FÍSICAS
-- ============================================================

-- Sesión fuerza 1 (-89 tren superior)
INSERT OR IGNORE INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 4, 10
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = date('now', '-89 days')
  AND e.owner_id=1 AND e.name='Pull-Up' AND e.deleted_at IS NULL LIMIT 1;

INSERT OR IGNORE INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 4, 12
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = date('now', '-89 days')
  AND e.owner_id=1 AND e.name='Dips' AND e.deleted_at IS NULL LIMIT 1;

INSERT OR IGNORE INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 3, 15
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = date('now', '-89 days')
  AND e.owner_id=1 AND e.name='Push-Up' AND e.deleted_at IS NULL LIMIT 1;

-- Sesión fuerza 2 (-85 tren inferior)
INSERT OR IGNORE INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 4, 8
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = date('now', '-85 days')
  AND e.owner_id=1 AND e.name='Barbell Squat' AND e.deleted_at IS NULL LIMIT 1;

INSERT OR IGNORE INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 3, 8
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = date('now', '-85 days')
  AND e.owner_id=1 AND e.name='Barbell Deadlift' AND e.deleted_at IS NULL LIMIT 1;

INSERT OR IGNORE INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 3, 12
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = date('now', '-85 days')
  AND e.owner_id=1 AND e.name='Hip Thrust' AND e.deleted_at IS NULL LIMIT 1;

-- Sesión cardio 1 (-87)
INSERT OR IGNORE INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps, duration_seconds)
SELECT ps.id, e.id, 1, NULL, 1500
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = date('now', '-87 days')
  AND e.owner_id=1 AND e.name='Mountain Climber' AND e.deleted_at IS NULL LIMIT 1;

-- Sesión fuerza 3 (-80 compuesta)
INSERT OR IGNORE INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 4, 8
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = date('now', '-80 days')
  AND e.owner_id=1 AND e.name='Bench Press' AND e.deleted_at IS NULL LIMIT 1;

INSERT OR IGNORE INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 4, 8
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = date('now', '-80 days')
  AND e.owner_id=1 AND e.name='Barbell Row' AND e.deleted_at IS NULL LIMIT 1;

INSERT OR IGNORE INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 3, 10
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = date('now', '-80 days')
  AND e.owner_id=1 AND e.name='Overhead Press' AND e.deleted_at IS NULL LIMIT 1;

-- Sesión fuerza 4 (-75 core)
INSERT OR IGNORE INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 5, 8
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = date('now', '-75 days')
  AND e.owner_id=1 AND e.name='Pull-Up' AND e.deleted_at IS NULL LIMIT 1;

INSERT OR IGNORE INTO physical_session_exercise (physical_session_id, exercise_id, sets, duration_seconds, reps)
SELECT ps.id, e.id, 3, 45, NULL
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = date('now', '-75 days')
  AND e.owner_id=1 AND e.name='Hollow Body Hold' AND e.deleted_at IS NULL LIMIT 1;

-- Sesión HIIT 1 (-73 kettlebell)
INSERT OR IGNORE INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 4, 20
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = date('now', '-73 days')
  AND e.owner_id=1 AND e.name='Kettlebell Swing' AND e.deleted_at IS NULL LIMIT 1;

INSERT OR IGNORE INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 4, 12
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = date('now', '-73 days')
  AND e.owner_id=1 AND e.name='Goblet Squat' AND e.deleted_at IS NULL LIMIT 1;

-- Sesión flexibilidad 1 (-84)
INSERT OR IGNORE INTO physical_session_exercise (physical_session_id, exercise_id, sets, duration_seconds, reps)
SELECT ps.id, e.id, 3, 60, NULL
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = date('now', '-84 days')
  AND e.owner_id=1 AND e.name='Plank' AND e.deleted_at IS NULL LIMIT 1;

-- Sesión HIIT 2 (-54 battle ropes)
INSERT OR IGNORE INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps, duration_seconds)
SELECT ps.id, e.id, 5, NULL, 40
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = date('now', '-54 days')
  AND e.owner_id=1 AND e.name='Battle Ropes' AND e.deleted_at IS NULL LIMIT 1;

INSERT OR IGNORE INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 5, 10
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = date('now', '-54 days')
  AND e.owner_id=1 AND e.name='Burpee' AND e.deleted_at IS NULL LIMIT 1;

-- Sesión fuerza 7 (-57 pierna)
INSERT OR IGNORE INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 4, 10
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = date('now', '-57 days')
  AND e.owner_id=1 AND e.name='Romanian Deadlift' AND e.deleted_at IS NULL LIMIT 1;

INSERT OR IGNORE INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 4, 12
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = date('now', '-57 days')
  AND e.owner_id=1 AND e.name='Hip Thrust' AND e.deleted_at IS NULL LIMIT 1;

-- Sesión HIIT 3 (-38 box jumps)
INSERT OR IGNORE INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 6, 8
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = date('now', '-38 days')
  AND e.owner_id=1 AND e.name='Box Jump' AND e.deleted_at IS NULL LIMIT 1;

INSERT OR IGNORE INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 5, 10
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = date('now', '-38 days')
  AND e.owner_id=1 AND e.name='Burpee' AND e.deleted_at IS NULL LIMIT 1;

-- Sesión fuerza 9 (-42)
INSERT OR IGNORE INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 4, 10
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = date('now', '-42 days')
  AND e.owner_id=1 AND e.name='Pull-Up' AND e.deleted_at IS NULL LIMIT 1;

INSERT OR IGNORE INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 4, 12
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = date('now', '-42 days')
  AND e.owner_id=1 AND e.name='Dips' AND e.deleted_at IS NULL LIMIT 1;

-- Sesión fuerza 11 (-28 PR pull-ups)
INSERT OR IGNORE INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 5, 12
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = date('now', '-28 days')
  AND e.owner_id=1 AND e.name='Pull-Up' AND e.deleted_at IS NULL LIMIT 1;

INSERT OR IGNORE INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 3, 8
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = date('now', '-28 days')
  AND e.owner_id=1 AND e.name='Barbell Row' AND e.deleted_at IS NULL LIMIT 1;

-- Sesión HIIT 4 (-19)
INSERT OR IGNORE INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 4, 15
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = date('now', '-19 days')
  AND e.owner_id=1 AND e.name='Burpee' AND e.deleted_at IS NULL LIMIT 1;

INSERT OR IGNORE INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 4, 8
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = date('now', '-19 days')
  AND e.owner_id=1 AND e.name='Pull-Up' AND e.deleted_at IS NULL LIMIT 1;

-- Sesión fuerza 13 (-14 pressing + core)
INSERT OR IGNORE INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 4, 10
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = date('now', '-14 days')
  AND e.owner_id=1 AND e.name='Dips' AND e.deleted_at IS NULL LIMIT 1;

INSERT OR IGNORE INTO physical_session_exercise (physical_session_id, exercise_id, sets, duration_seconds, reps)
SELECT ps.id, e.id, 4, 30, NULL
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = date('now', '-14 days')
  AND e.owner_id=1 AND e.name='L-Sit' AND e.deleted_at IS NULL LIMIT 1;

-- Sesión HIIT 5 (-5 kettlebell + jumps)
INSERT OR IGNORE INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 5, 20
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = date('now', '-5 days')
  AND e.owner_id=1 AND e.name='Kettlebell Swing' AND e.deleted_at IS NULL LIMIT 1;

INSERT OR IGNORE INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 5, 8
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = date('now', '-5 days')
  AND e.owner_id=1 AND e.name='Box Jump' AND e.deleted_at IS NULL LIMIT 1;

-- Sesión fuerza 14 (-7 descarga)
INSERT OR IGNORE INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 3, 12
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = date('now', '-7 days')
  AND e.owner_id=1 AND e.name='Pull-Up' AND e.deleted_at IS NULL LIMIT 1;

INSERT OR IGNORE INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 3, 15
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = date('now', '-7 days')
  AND e.owner_id=1 AND e.name='Push-Up' AND e.deleted_at IS NULL LIMIT 1;
