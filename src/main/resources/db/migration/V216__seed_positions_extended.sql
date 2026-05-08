-- V216__seed_positions_extended.sql
-- 25 posiciones adicionales (owner_id=1). Complementa V200 que ya tiene 40.
-- Añade guards específicos de gi, posiciones de leg lock avanzadas,
-- posiciones de wrestler y transiciones importantes.

INSERT INTO position (owner_id, name, type, description, visibility, created_at, updated_at, version) VALUES

-- STANDING (4)
(1, 'Sprawl',                       'STANDING',       'Defensa de derribo: caderas abajo y pies atrás para quitar la tracción al oponente que intenta double/single leg.',           'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, 'Fireman Carry Position',        'STANDING',       'Control del hombro y pierna del oponente para la proyección de bombero. Gi y no-gi.',                                         'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, 'Collar Tie',                    'STANDING',       'Control del cuello con una mano desde posición de pie. Base para snaps, underhooks y derribos de judo.',                      'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, 'Rear Standing',                 'STANDING',       'Detrás del oponente de pie, sin ganchos aún. Transición a back mount o double underhook suplex.',                             'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

-- TOP (7)
(1, 'Technical Mount',               'TOP',            'Mount con una rodilla en el suelo y el otro pie plantado. Mayor estabilidad y control que mount estándar.',                   'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, 'Leg Knot',                      'TOP',            'Control de pierna desde top en leg entanglements. Inmovilización para ataques de kneebar o toe hold.',                       'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, 'Truck',                         'TOP',            'Posición 10th Planet: un gancho dentro de las piernas del oponente en turtle. Base para crotch ripper y back take.',         'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, 'Mount with Arm Trap',           'TOP',            'Mount con el brazo del oponente atrapado bajo la rodilla. Maximiza el control antes de ataques al cuello.',                  'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, 'Reverse Scarf Hold',            'TOP',            'Kesa gatame invertido: cabeza controlada mirando hacia los pies. Ataques omoplata y americana invertida.',                   'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, 'Modified Side Control',         'TOP',            'Side control con la cadera baja y el peso distribuido para resistir recuperaciones de guardia.',                              'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, 'Dogfight',                      'TOP',            'Posición de batalla de rodillas: ambos en all-fours compitiendo underhooks. Transición a side control o back.',              'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

-- GROUND_NEUTRAL (5)
(1, 'Inside Heel Hook Position',     'GROUND_NEUTRAL', 'Ashi garami con control interno para inside heel hook. Rodilla del oponente rotada hacia afuera.',                           'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, 'Saddle',                        'GROUND_NEUTRAL', 'Honey hole / saddle: configuración de piernas para heel hooks con control superior del cuero. La más ventajosa.',            'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, 'Double Outside Ashi',           'GROUND_NEUTRAL', 'Ashi garami doble exterior. Presión extrema sobre ambas rodillas. Avanzado, sistema ADCC moderno.',                          'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, 'Front Headlock',                'GROUND_NEUTRAL', 'Control de cabeza frontal (no-gi). Base para guillotinas, darcé, anaconda y Peruvian necktie.',                              'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, 'Kimura Trap',                   'GROUND_NEUTRAL', 'Brazo del oponente controlado en kimura trap. Sistema ofensivo que conecta múltiples posiciones top.',                       'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

-- BOTTOM (9)
(1, 'Squid Guard',                   'BOTTOM',         'Variante avanzada de DLR con grip en el pantalón. Creada por Marcelo Garcia. Barridos y back takes.',                        'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, 'Shin-on-Shin Guard',            'BOTTOM',         'Espinilla contra espinilla del oponente. Guardia de entrada a SLX, DLR y barridos de tracción.',                            'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, 'Octopus Guard',                 'BOTTOM',         'Control del brazo y pierna del mismo lado desde abajo. Back take y barridos usando el momentum del oponente.',               'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, 'Mantis Guard',                  'BOTTOM',         'Guardia con el pie en la cadera y el otro controlando la manga. Creada por Robson Moura. Barridos y triangles.',             'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, 'Williams Guard',                'BOTTOM',         'Pierna enroscada al cuello del oponente con control de brazo. Sistema de Shawn Williams. No requiere solapa.',               'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, 'Coyote Guard',                  'BOTTOM',         'DLR con el pie en el hueco de la rodilla del oponente. Barridos de arrastre y transiciones a leg locks.',                   'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, 'Inverted Guard',                'BOTTOM',         'Guardia invertida sobre los hombros mirando hacia abajo. Base para berimbolo y transiciones de leg lock.',                   'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, 'Half Butterfly Guard',          'BOTTOM',         'Combinación de half guard con un gancho mariposa. Versatilidad de ataques entre los dos sistemas.',                         'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(1, 'Seated Rear Guard',             'BOTTOM',         'Sentado con la espalda al oponente, gestionando el back mount antes de que inserte los ganchos.',                            'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);
