-- V224__seed_demo_data.sql
-- Batería de datos de demostración: 90 días de sesiones BJJ y físicas con técnicas y ejercicios.
-- Todos los datos son para owner_id=1.

-- ============================================================
-- SESIONES BJJ (últimos 90 días, ~3-4 por semana)
-- ============================================================

INSERT INTO training_session (owner_id, session_date, duration_minutes, location, intensity, notes_markdown, created_at, updated_at, version)
VALUES
-- Semana 13 (hace ~90 días)
(1, CURRENT_DATE - 90, 90, 'Gimnasio',    'HIGH',   'Sparring intenso. Trabajé mucho la guardia cerrada.',          CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 88, 75, 'Gimnasio',    'MEDIUM', 'Clase técnica: triángulos y armbars encadenados.',             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 86, 60, 'Gimnasio',    'LOW',    'Técnica. Enfocado en pasadas de guardia.',                     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
-- Semana 12
(1, CURRENT_DATE - 83, 90, 'Gimnasio',    'HIGH',   'Competición interna. 4 luchas.',                               CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 81, 75, 'Gimnasio',    'MEDIUM', 'Clase: leg locks básicos y defensa.',                          CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 79, 60, 'Casa',        'LOW',    'Solo drilling. Movimiento de caderas y shrimping.',            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
-- Semana 11
(1, CURRENT_DATE - 76, 90, 'Gimnasio',    'HIGH',   'Sparring. Trabajé tomas de espalda.',                          CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 74, 75, 'Gimnasio',    'MEDIUM', 'Clase: barridas desde half guard.',                            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 72, 90, 'Gimnasio',    'HIGH',   'Open mat. Mucho juego con guillotinas.',                       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 71, 60, 'Gimnasio',    'LOW',    'Técnica: derribos y takedowns.',                               CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
-- Semana 10
(1, CURRENT_DATE - 69, 90, 'Gimnasio',    'HIGH',   'Sparring duro. Shoulder locks encadenados.',                   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 67, 75, 'Gimnasio',    'MEDIUM', 'Clase: escapadas de mount y side control.',                    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 65, 60, 'Gimnasio',    'LOW',    'Técnica: chokes desde guardia.',                               CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
-- Semana 9
(1, CURRENT_DATE - 62, 90, 'Gimnasio',    'HIGH',   'Open mat. Triángulos y omoplatas.',                            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 60, 75, 'Gimnasio',    'MEDIUM', 'Clase: pasadas de guardia spider y lasso.',                    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 58, 90, 'Gimnasio',    'HIGH',   'Sparring. Mucho back take desde turtle.',                      CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
-- Semana 8
(1, CURRENT_DATE - 55, 90, 'Gimnasio',    'HIGH',   'Competición externa. 3 luchas ganadas.',                       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 53, 75, 'Gimnasio',    'MEDIUM', 'Clase técnica: RNC y bow and arrow.',                          CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 51, 60, 'Casa',        'LOW',    'Drilling: armbars desde guardia.',                             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
-- Semana 7
(1, CURRENT_DATE - 48, 90, 'Gimnasio',    'HIGH',   'Sparring intenso. Leg locks ataques y defensas.',              CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 46, 75, 'Gimnasio',    'MEDIUM', 'Clase: barridas desde X-guard.',                               CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 44, 90, 'Gimnasio',    'HIGH',   'Open mat. Juego de guardia abierta.',                          CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 43, 60, 'Gimnasio',    'LOW',    'Técnica: double leg y penetration step.',                      CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
-- Semana 6
(1, CURRENT_DATE - 41, 90, 'Gimnasio',    'HIGH',   'Sparring. Kimuras y americanas encadenadas.',                  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 39, 75, 'Gimnasio',    'MEDIUM', 'Clase: D''Arce y Anaconda choke.',                             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 37, 60, 'Gimnasio',    'LOW',    'Técnica: Omoplata y baratoplata.',                             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
-- Semana 5
(1, CURRENT_DATE - 34, 90, 'Gimnasio',    'HIGH',   'Open mat. Mucho trabajo en el suelo.',                         CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 32, 75, 'Gimnasio',    'MEDIUM', 'Clase: tomas de espalda desde guardia.',                       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 30, 90, 'Gimnasio',    'HIGH',   'Sparring duro. Triangulos desde guardia invertida.',           CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
-- Semana 4
(1, CURRENT_DATE - 27, 90, 'Gimnasio',    'HIGH',   'Open mat. Leg locks y kneebars.',                              CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 25, 75, 'Gimnasio',    'MEDIUM', 'Clase: pasadas pressure y toreando.',                          CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 23, 60, 'Gimnasio',    'LOW',    'Técnica: guillotinas arm-in y high elbow.',                    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
-- Semana 3
(1, CURRENT_DATE - 20, 90, 'Gimnasio',    'HIGH',   'Sparring. Mucho trabajo de top game.',                         CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 18, 75, 'Gimnasio',    'MEDIUM', 'Clase: escapadas de guardia cerrada.',                         CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 16, 90, 'Gimnasio',    'HIGH',   'Open mat. Back mount ataques.',                                CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
-- Semana 2
(1, CURRENT_DATE - 13, 90, 'Gimnasio',    'HIGH',   'Sparring intenso. Mejor ritmo en guard passing.',              CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 11, 75, 'Gimnasio',    'MEDIUM', 'Clase: single leg y double leg desde clinch.',                 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 9,  60, 'Gimnasio',    'LOW',    'Técnica: wristlocks y pequeñas llaves.',                       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
-- Semana 1 (última semana)
(1, CURRENT_DATE - 6,  90, 'Gimnasio',    'HIGH',   'Open mat. Fui de competición. Buen día.',                      CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 4,  75, 'Gimnasio',    'MEDIUM', 'Clase: triángulos desde X-guard.',                             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 2,  90, 'Gimnasio',    'HIGH',   'Sparring. Armbar desde guardia muy fluido hoy.',               CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

-- ============================================================
-- TÉCNICAS TRABAJADAS EN SESIONES BJJ
-- Relacionamos por nombre de técnica usando subqueries.
-- ============================================================

-- Sesión 1 (CURRENT_DATE - 90)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count, notes_markdown)
SELECT ts.id, t.id, 20, 'Drilling básico'
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 90
  AND t.owner_id=1 AND t.name='Armbar' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 90
  AND t.owner_id=1 AND t.name='Triangle Choke' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 12
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 90
  AND t.owner_id=1 AND t.name='Kimura' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 2 (CURRENT_DATE - 88)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 25
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 88
  AND t.owner_id=1 AND t.name='Triangle Choke' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 88
  AND t.owner_id=1 AND t.name='Armbar' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 3 (CURRENT_DATE - 86)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 18
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 86
  AND t.owner_id=1 AND t.name='Torreando Pass' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 86
  AND t.owner_id=1 AND t.name='Knee Slice Pass' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 4 (CURRENT_DATE - 83)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 10
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 83
  AND t.owner_id=1 AND t.name='Rear Naked Choke' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 8
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 83
  AND t.owner_id=1 AND t.name='Armbar' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 6
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 83
  AND t.owner_id=1 AND t.name='Double Leg Takedown' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 5 (CURRENT_DATE - 81)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 22
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 81
  AND t.owner_id=1 AND t.name='Heel Hook' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 18
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 81
  AND t.owner_id=1 AND t.name='Kneebar' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 6 (CURRENT_DATE - 76 — tomas de espalda)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 76
  AND t.owner_id=1 AND t.name='Rear Naked Choke' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 76
  AND t.owner_id=1 AND t.name='Body Triangle to Back Take' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 7 (CURRENT_DATE - 74 — barridas)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 74
  AND t.owner_id=1 AND t.name='Scissor Sweep' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 18
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 74
  AND t.owner_id=1 AND t.name='Hip Bump Sweep' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 74
  AND t.owner_id=1 AND t.name='Flower Sweep' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 8 (CURRENT_DATE - 72 — guillotinas)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 72
  AND t.owner_id=1 AND t.name='Guillotine Choke' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 12
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 72
  AND t.owner_id=1 AND t.name='Kimura' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 9 (CURRENT_DATE - 71 — derribos)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 71
  AND t.owner_id=1 AND t.name='Double Leg Takedown' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 71
  AND t.owner_id=1 AND t.name='Single Leg Takedown' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 10 (CURRENT_DATE - 69 — shoulder locks)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 18
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 69
  AND t.owner_id=1 AND t.name='Kimura' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 18
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 69
  AND t.owner_id=1 AND t.name='Americana' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 12
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 69
  AND t.owner_id=1 AND t.name='Omoplata' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 11 (CURRENT_DATE - 67 — escapadas)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 25
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 67
  AND t.owner_id=1 AND t.name='Mount Escape (Elbow-Knee)' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 67
  AND t.owner_id=1 AND t.name='Side Control Escape (Shrimp)' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 12 (CURRENT_DATE - 65 — chokes desde guardia)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 65
  AND t.owner_id=1 AND t.name='Triangle Choke' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 65
  AND t.owner_id=1 AND t.name='Guillotine Choke' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 13 (CURRENT_DATE - 62 — triángulos y omoplatas)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 62
  AND t.owner_id=1 AND t.name='Triangle Choke' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 62
  AND t.owner_id=1 AND t.name='Omoplata' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 14 (CURRENT_DATE - 60 — pasadas)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 60
  AND t.owner_id=1 AND t.name='Torreando Pass' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 18
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 60
  AND t.owner_id=1 AND t.name='Over-Under Pass' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 15 (CURRENT_DATE - 58 — back takes)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 58
  AND t.owner_id=1 AND t.name='Seat Belt Grip Back Take' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 58
  AND t.owner_id=1 AND t.name='Rear Naked Choke' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 16 (CURRENT_DATE - 55 — competición)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 8
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 55
  AND t.owner_id=1 AND t.name='Armbar' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 6
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 55
  AND t.owner_id=1 AND t.name='Double Leg Takedown' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 17 (CURRENT_DATE - 53 — RNC y bow and arrow)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 53
  AND t.owner_id=1 AND t.name='Rear Naked Choke' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 53
  AND t.owner_id=1 AND t.name='Bow and Arrow Choke' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 18 (CURRENT_DATE - 51 — armbars drilling)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 30
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 51
  AND t.owner_id=1 AND t.name='Armbar' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 19 (CURRENT_DATE - 48 — leg locks)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 48
  AND t.owner_id=1 AND t.name='Heel Hook' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 48
  AND t.owner_id=1 AND t.name='Kneebar' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 10
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 48
  AND t.owner_id=1 AND t.name='Toe Hold' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 20 (CURRENT_DATE - 46 — barridas X-guard)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 18
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 46
  AND t.owner_id=1 AND t.name='X-Guard Sweep' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 46
  AND t.owner_id=1 AND t.name='Scissor Sweep' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 21 (CURRENT_DATE - 44)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 44
  AND t.owner_id=1 AND t.name='Triangle Choke' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 12
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 44
  AND t.owner_id=1 AND t.name='Armbar' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 22 (CURRENT_DATE - 43 — derribos)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 43
  AND t.owner_id=1 AND t.name='Double Leg Takedown' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 43
  AND t.owner_id=1 AND t.name='Single Leg Takedown' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 23 (CURRENT_DATE - 41 — kimuras y americanas)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 41
  AND t.owner_id=1 AND t.name='Kimura' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 18
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 41
  AND t.owner_id=1 AND t.name='Americana' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 24 (CURRENT_DATE - 39 — D'Arce y Anaconda)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 39
  AND t.owner_id=1 AND t.name='D''Arce Choke' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 39
  AND t.owner_id=1 AND t.name='Anaconda Choke' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 25 (CURRENT_DATE - 37 — Omoplata y baratoplata)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 18
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 37
  AND t.owner_id=1 AND t.name='Omoplata' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 12
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 37
  AND t.owner_id=1 AND t.name='Kimura' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 26 (CURRENT_DATE - 34)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 34
  AND t.owner_id=1 AND t.name='Rear Naked Choke' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 12
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 34
  AND t.owner_id=1 AND t.name='Bow and Arrow Choke' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 10
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 34
  AND t.owner_id=1 AND t.name='Body Triangle to Back Take' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 27 (CURRENT_DATE - 32 — back takes)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 18
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 32
  AND t.owner_id=1 AND t.name='Seat Belt Grip Back Take' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 32
  AND t.owner_id=1 AND t.name='Rear Naked Choke' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 28 (CURRENT_DATE - 30 — triángulos)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 25
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 30
  AND t.owner_id=1 AND t.name='Triangle Choke' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 12
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 30
  AND t.owner_id=1 AND t.name='Armbar' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 29 (CURRENT_DATE - 27 — leg locks)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 18
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 27
  AND t.owner_id=1 AND t.name='Heel Hook' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 12
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 27
  AND t.owner_id=1 AND t.name='Kneebar' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 30 (CURRENT_DATE - 25 — pasadas)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 25
  AND t.owner_id=1 AND t.name='Torreando Pass' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 18
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 25
  AND t.owner_id=1 AND t.name='Over-Under Pass' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 31 (CURRENT_DATE - 23 — guillotinas)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 22
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 23
  AND t.owner_id=1 AND t.name='Guillotine Choke' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 32 (CURRENT_DATE - 20)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 20
  AND t.owner_id=1 AND t.name='Knee Slice Pass' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 12
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 20
  AND t.owner_id=1 AND t.name='Torreando Pass' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 33 (CURRENT_DATE - 18 — escapadas)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 25
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 18
  AND t.owner_id=1 AND t.name='Mount Escape (Elbow-Knee)' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 18
  AND t.owner_id=1 AND t.name='Side Control Escape (Shrimp)' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 34 (CURRENT_DATE - 16 — back mount)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 20
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 16
  AND t.owner_id=1 AND t.name='Rear Naked Choke' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 16
  AND t.owner_id=1 AND t.name='Seat Belt Grip Back Take' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 35 (CURRENT_DATE - 13)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 18
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 13
  AND t.owner_id=1 AND t.name='Over-Under Pass' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 13
  AND t.owner_id=1 AND t.name='Armbar' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 36 (CURRENT_DATE - 11 — derribos)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 22
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 11
  AND t.owner_id=1 AND t.name='Single Leg Takedown' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 18
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 11
  AND t.owner_id=1 AND t.name='Double Leg Takedown' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 37 (CURRENT_DATE - 9 — wristlocks)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 9
  AND t.owner_id=1 AND t.name='Kimura' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 12
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 9
  AND t.owner_id=1 AND t.name='Americana' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 38 (CURRENT_DATE - 6)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 6
  AND t.owner_id=1 AND t.name='Triangle Choke' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 12
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 6
  AND t.owner_id=1 AND t.name='Guillotine Choke' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 39 (CURRENT_DATE - 4 — triángulos X-guard)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 22
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 4
  AND t.owner_id=1 AND t.name='Triangle Choke' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 4
  AND t.owner_id=1 AND t.name='X-Guard Sweep' AND t.deleted_at IS NULL
LIMIT 1;

-- Sesión 40 (CURRENT_DATE - 2 — armbar desde guardia)
INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 25
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 2
  AND t.owner_id=1 AND t.name='Armbar' AND t.deleted_at IS NULL
LIMIT 1;

INSERT INTO training_session_technique (training_session_id, technique_id, rep_count)
SELECT ts.id, t.id, 15
FROM training_session ts, technique t
WHERE ts.owner_id=1 AND ts.session_date = CURRENT_DATE - 2
  AND t.owner_id=1 AND t.name='Triangle Choke' AND t.deleted_at IS NULL
LIMIT 1;

-- ============================================================
-- SESIONES FÍSICAS (últimos 90 días, ~2-3 por semana)
-- ============================================================

INSERT INTO physical_session (owner_id, session_date, session_type, title, duration_minutes, notes, created_at, updated_at, version)
VALUES
-- Fuerza (STRENGTH)
(1, CURRENT_DATE - 89, 'STRENGTH',    'Fuerza tren superior', 60, 'Pull-ups, dips, press. Buena sesión.',             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 85, 'STRENGTH',    'Fuerza tren inferior', 55, 'Squats, deadlifts, hip thrust.',                  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 80, 'STRENGTH',    'Fuerza compuesta',     65, 'Bench press, barbell row, overhead press.',       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 75, 'STRENGTH',    'Fuerza + core',        60, 'Pull-ups, hollow body, L-sit.',                   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 70, 'STRENGTH',    'Pressing day',         50, 'Push-ups variantes, dips weighted.',              CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 64, 'STRENGTH',    'Pulling day',          55, 'Pull-up negatives, Barbell row, face pulls.',     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 57, 'STRENGTH',    'Pierna + glúteo',      60, 'Bulgarian split squat, hip thrust, RDL.',         CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 50, 'STRENGTH',    'Fuerza total',         65, 'Grandes patrones de movimiento.',                 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 42, 'STRENGTH',    'Fuerza tren superior', 60, 'Progresión: más peso que hace 3 semanas.',        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 35, 'STRENGTH',    'Fuerza + accesorios',  55, 'Compuestos + trabajo unilateral.',                CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 28, 'STRENGTH',    'Fuerza total',         60, 'PR en pull-ups: 12 reps limpias.',                CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 21, 'STRENGTH',    'Tren inferior',        55, 'Deadlift + sentadillas Búlgaras.',                CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 14, 'STRENGTH',    'Pressing + core',      60, 'Fondos, pino, planche lean.',                     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 7,  'STRENGTH',    'Fuerza tren superior', 55, 'Semana de descarga. Menor volumen.',               CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
-- Cardio
(1, CURRENT_DATE - 87, 'CARDIO',      'Running 5km',          35, '5km en 25 min. Buen ritmo aeróbico.',             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 82, 'CARDIO',      'Cycling 40min',        45, 'Bici estática. Zona 2 aeróbico.',                 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 77, 'CARDIO',      'Running 6km',          40, '6km. Trabajé el ritmo.',                          CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 66, 'CARDIO',      'Jump rope + running',  40, 'Comba 15min + carrera 20min.',                    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 56, 'CARDIO',      'Running 7km',          45, 'PR distancia. 7km seguidos.',                     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 45, 'CARDIO',      'Assault bike 20min',   25, 'Cardio de alta intensidad con bici.',             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 33, 'CARDIO',      'Running 6km',          40, 'Ritmo cómodo. Zona 2.',                           CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 22, 'CARDIO',      'Running 8km',          50, '8km. Mejor marca de la temporada.',               CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 10, 'CARDIO',      'Cycling 45min',        50, 'Recuperación activa.',                            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 3,  'CARDIO',      'Running 5km',          35, 'Última sesión antes del entrenamiento.',          CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
-- Flexibilidad
(1, CURRENT_DATE - 84, 'FLEXIBILITY', 'Movilidad de cadera',  40, 'Hip flexor, piriforme, aductores.',               CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 78, 'FLEXIBILITY', 'Estiramiento global',  35, 'Cadena posterior, hombros, cuello.',              CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 63, 'FLEXIBILITY', 'Yoga flow',            45, 'Clase de yoga. Mucho trabajo de cadera.',         CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 47, 'FLEXIBILITY', 'Movilidad articular',  35, 'Hombros, caderas, tobillo, muñecas.',             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 29, 'FLEXIBILITY', 'Deep stretching',      40, 'Estiramiento profundo. Splits progresión.',       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 15, 'FLEXIBILITY', 'Yoga + movilidad',     45, 'Combinado yoga y trabajo de movilidad.',          CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
-- HIIT
(1, CURRENT_DATE - 73, 'HIIT',        'Kettlebell circuit',   35, 'Swings, goblet squat, snatches. 4 rondas.',       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 54, 'HIIT',        'Battle ropes + burpees', 30, 'Intervalos 40/20. Muy exigente.',               CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 38, 'HIIT',        'Box jumps + sprints',  30, 'Potencia explosiva. 6 series.',                   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 19, 'HIIT',        'WOD crossfit style',   35, 'AMRAP 20min: burpees, wall balls, pull-ups.',     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, CURRENT_DATE - 5,  'HIIT',        'Kettlebell + jumps',   30, 'Explosividad. Swings y box jumps.',               CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

-- ============================================================
-- EJERCICIOS TRABAJADOS EN SESIONES FÍSICAS
-- ============================================================

-- Sesión fuerza 1 (CURRENT_DATE - 89)
INSERT INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 4, 10
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = CURRENT_DATE - 89
  AND e.owner_id=1 AND e.name='Pull-Up' AND e.deleted_at IS NULL
LIMIT 1;

INSERT INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 4, 12
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = CURRENT_DATE - 89
  AND e.owner_id=1 AND e.name='Dips' AND e.deleted_at IS NULL
LIMIT 1;

INSERT INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 3, 15
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = CURRENT_DATE - 89
  AND e.owner_id=1 AND e.name='Push-Up' AND e.deleted_at IS NULL
LIMIT 1;

-- Sesión fuerza 2 (CURRENT_DATE - 85)
INSERT INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 4, 8
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = CURRENT_DATE - 85
  AND e.owner_id=1 AND e.name='Barbell Squat' AND e.deleted_at IS NULL
LIMIT 1;

INSERT INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 3, 8
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = CURRENT_DATE - 85
  AND e.owner_id=1 AND e.name='Barbell Deadlift' AND e.deleted_at IS NULL
LIMIT 1;

INSERT INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 3, 12
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = CURRENT_DATE - 85
  AND e.owner_id=1 AND e.name='Hip Thrust' AND e.deleted_at IS NULL
LIMIT 1;

-- Sesión cardio 1 (CURRENT_DATE - 87)
INSERT INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps, duration_seconds)
SELECT ps.id, e.id, 1, NULL, 1500
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = CURRENT_DATE - 87
  AND e.owner_id=1 AND e.name='Mountain Climber' AND e.deleted_at IS NULL
LIMIT 1;

-- Sesión HIIT 1 (CURRENT_DATE - 73)
INSERT INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 4, 20
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = CURRENT_DATE - 73
  AND e.owner_id=1 AND e.name='Kettlebell Swing' AND e.deleted_at IS NULL
LIMIT 1;

INSERT INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 4, 12
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = CURRENT_DATE - 73
  AND e.owner_id=1 AND e.name='Goblet Squat' AND e.deleted_at IS NULL
LIMIT 1;

-- Sesión flexibilidad 1 (CURRENT_DATE - 84)
INSERT INTO physical_session_exercise (physical_session_id, exercise_id, sets, duration_seconds, reps)
SELECT ps.id, e.id, 3, 60, NULL
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = CURRENT_DATE - 84
  AND e.owner_id=1 AND e.name='Plank' AND e.deleted_at IS NULL
LIMIT 1;

-- Sesión fuerza 3 (CURRENT_DATE - 80)
INSERT INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 4, 8
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = CURRENT_DATE - 80
  AND e.owner_id=1 AND e.name='Bench Press' AND e.deleted_at IS NULL
LIMIT 1;

INSERT INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 4, 8
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = CURRENT_DATE - 80
  AND e.owner_id=1 AND e.name='Barbell Row' AND e.deleted_at IS NULL
LIMIT 1;

INSERT INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 3, 10
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = CURRENT_DATE - 80
  AND e.owner_id=1 AND e.name='Overhead Press' AND e.deleted_at IS NULL
LIMIT 1;

-- Sesión HIIT 2 (CURRENT_DATE - 54)
INSERT INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps, duration_seconds)
SELECT ps.id, e.id, 5, NULL, 40
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = CURRENT_DATE - 54
  AND e.owner_id=1 AND e.name='Battle Ropes' AND e.deleted_at IS NULL
LIMIT 1;

INSERT INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 5, 10
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = CURRENT_DATE - 54
  AND e.owner_id=1 AND e.name='Burpee' AND e.deleted_at IS NULL
LIMIT 1;

-- Sesión HIIT 3 (CURRENT_DATE - 38)
INSERT INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 6, 8
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = CURRENT_DATE - 38
  AND e.owner_id=1 AND e.name='Box Jump' AND e.deleted_at IS NULL
LIMIT 1;

-- Sesión fuerza última (CURRENT_DATE - 7)
INSERT INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 3, 12
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = CURRENT_DATE - 7
  AND e.owner_id=1 AND e.name='Pull-Up' AND e.deleted_at IS NULL
LIMIT 1;

INSERT INTO physical_session_exercise (physical_session_id, exercise_id, sets, reps)
SELECT ps.id, e.id, 3, 15
FROM physical_session ps, exercise e
WHERE ps.owner_id=1 AND ps.session_date = CURRENT_DATE - 7
  AND e.owner_id=1 AND e.name='Push-Up' AND e.deleted_at IS NULL
LIMIT 1;
