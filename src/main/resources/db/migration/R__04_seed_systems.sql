-- R__04_seed_systems.sql
-- Sistemas/guards BJJ del catálogo público (owner_id=1). Idempotente.
-- Consolida: V207, V209 (flow definitions vacíos se rellenan cuando haya datos reales).
-- Depende de: R__02_seed_positions.sql

INSERT INTO system (owner_id, name, description, anchor_position_id, flow_definition, flow_schema_version, visibility, created_at, updated_at, version)
VALUES
(1, 'Sistema De La Riva',
 'El sistema de guardia más icónico del BJJ moderno. Creado por Ricardo de La Riva. Basado en el gancho externo en la pierna delantera del oponente. Incluye barridos clásicos, berimbolo, toma de espalda y transiciones a single leg X y 50/50.',
 (SELECT id FROM position WHERE name='De La Riva Guard' AND owner_id=1),
 '{"nodes":[],"edges":[]}', 'v1', 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Spider Guard System',
 'Guardia gi de control con pies en bíceps y grips de manga. Ideal para atletas con piernas largas o flexibilidad. Ralentiza passing agresivo. Arsenal: triángulos desde spider, omoplata, spider sweep, collar-sleeve transitions.',
 (SELECT id FROM position WHERE name='Spider Guard' AND owner_id=1),
 '{"nodes":[],"edges":[]}', 'v1', 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Lasso Guard System',
 'Guardia gi que enrola la pierna en el brazo del oponente controlándolo completamente. Dificulta el passing y genera ángulos únicos para barridos y ataques. Combinada con spider guard forma el sistema collar-sleeve-lasso.',
 (SELECT id FROM position WHERE name='Lasso Guard' AND owner_id=1),
 '{"nodes":[],"edges":[]}', 'v1', 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Leg Lock System',
 'El sistema de piernas más completo del grappling moderno. Desarrollado por John Danaher con Gordon Ryan y Garry Tonon. Entrada desde seated guard a ashi garami → SLX → heel hooks. Filosofía: posición antes que submission.',
 (SELECT id FROM position WHERE name='Single Leg X' AND owner_id=1),
 '{"nodes":[],"edges":[]}', 'v1', 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'K-Guard System',
 'Guardia moderna no-gi desarrollada por Ethan Crelinsten y Craig Jones. Combina gancho exterior DLR con control de tobillo. Excelente contra torreando y passing de presión. Entradas a heel hooks y barridos.',
 (SELECT id FROM position WHERE name='K-Guard' AND owner_id=1),
 '{"nodes":[],"edges":[]}', 'v1', 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Butterfly Guard System',
 'Sistema de guardia sentada neutral. El más efectivo para back takes en no-gi. Ganchos bajo los muslos del oponente. Arsenal: butterfly sweep, back take, kimura desde butterfly. Maestro absoluto: Marcelo Garcia.',
 (SELECT id FROM position WHERE name='Butterfly Guard' AND owner_id=1),
 '{"nodes":[],"edges":[]}', 'v1', 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Half Guard System',
 'Sistema clásico de media guardia con múltiples subsistemas: Z-guard (knee shield), deep half, lockdown (10th Planet). Muy popular desde principiantes hasta negros. Marcelo Garcia fue el mayor exponente en competición.',
 (SELECT id FROM position WHERE name='Half Guard' AND owner_id=1),
 '{"nodes":[],"edges":[]}', 'v1', 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Rubber Guard System',
 'Sistema de Eddie Bravo (10th Planet) basado en flexibilidad extrema: llevar la pierna al cuello del oponente desde closed guard. Posiciones clave: mission control, new york, chill dog. Submissions: gogoplata, electric chair, twister.',
 (SELECT id FROM position WHERE name='Rubber Guard' AND owner_id=1),
 '{"nodes":[],"edges":[]}', 'v1', 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Back Attack System',
 'El sistema de ataque desde espalda más completo. La espalda es la posición más dominante del BJJ. Incluye: seat belt control, body triangle, RNC, bow and arrow (gi). Filosofía Danaher: mantener la espalda siguiendo los hombros del oponente.',
 (SELECT id FROM position WHERE name='Back Mount' AND owner_id=1),
 '{"nodes":[],"edges":[]}', 'v1', 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'X-Guard System',
 'Sistema de Marcelo Garcia. La configuración en X controla ambas piernas del oponente dando acceso a barridos de elevación y technical stands. Muy efectivo contra oponentes que no se agachan. Transiciones desde butterfly y SLX.',
 (SELECT id FROM position WHERE name='X-Guard' AND owner_id=1),
 '{"nodes":[],"edges":[]}', 'v1', 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)

ON CONFLICT (owner_id, name) WHERE deleted_at IS NULL DO NOTHING;
