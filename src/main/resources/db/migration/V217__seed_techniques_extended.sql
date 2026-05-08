-- V217__seed_techniques_extended.sql
-- Técnicas adicionales que complementan V201-V206 (~100 técnicas nuevas).
-- Requiere V200, V201-V206 y V216 ejecutados primero.
-- start_position_id y end_position_id usan subqueries por nombre para robustez.

INSERT INTO technique (owner_id, name, category, description, minimum_belt, modality, start_position_id, end_position_id, visibility, created_at, updated_at, version) VALUES

-- =====================================================================
-- SUBMISSIONS adicionales (~35)
-- =====================================================================

-- Chokes adicionales
(1, 'Arm-In Guillotine',          'SUBMISSION', 'Guillotina con el brazo del oponente atrapado dentro. Más segura que la guillotina clásica. Muy popular en no-gi.',
    'BLUE',   'NOGI', (SELECT id FROM position WHERE name='Closed Guard'          AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'High Elbow Guillotine',      'SUBMISSION', 'Variante de guillotina con el codo elevado para maximizar la presión. Dominada por Marcelo Garcia. Letal en competición.',
    'BLUE',   'NOGI', (SELECT id FROM position WHERE name='Closed Guard'          AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Marcelotine',                'SUBMISSION', 'Guillotina sin el brazo con el codo cruzando por encima del hombro. Variante de Marcelo Garcia de máxima eficacia.',
    'PURPLE', 'NOGI', (SELECT id FROM position WHERE name='Closed Guard'          AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Von Flue Choke',             'SUBMISSION', 'Estrangulación de contraataque a guillotina: bajar el hombro contra el cuello del oponente que intenta guillotina.',
    'PURPLE', 'NOGI', (SELECT id FROM position WHERE name='Side Control'          AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Lapel Choke',                'SUBMISSION', 'Estrangulación usando la solapa del gi. Grip en solapa propia o del oponente. Múltiples variantes según posición.',
    'BLUE',   'GI',   (SELECT id FROM position WHERE name='Back Mount'            AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Brabo Choke',                'SUBMISSION', 'Estrangulación usando la solapa para crear el efecto de guillotina. Gi. También llamado loop choke con solapa.',
    'BLUE',   'GI',   (SELECT id FROM position WHERE name='Side Control'          AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Twister',                    'SUBMISSION', 'Torsión espinal desde control lateral de espalda. Icónico del sistema 10th Planet de Eddie Bravo. Muy raro en competición.',
    'BROWN',  'NOGI', (SELECT id FROM position WHERE name='Back Mount'            AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Arm Triangle Choke',         'SUBMISSION', 'Estrangulación triángulo con los brazos atrapando cabeza y hombro. Desde mount o side control. Muy efectiva en MMA.',
    'BLUE',   'BOTH', (SELECT id FROM position WHERE name='Full Mount'            AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Scarf Choke',                'SUBMISSION', 'Estrangulación desde kesa gatame usando el peso del cuerpo sobre el cuello. Gi y no-gi. Difícil de defender.',
    'BLUE',   'BOTH', (SELECT id FROM position WHERE name='Scarf Hold'            AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Crucifix Choke',             'SUBMISSION', 'Estrangulación desde posición crucifix usando RNC mientras ambos brazos del oponente están controlados.',
    'PURPLE', 'BOTH', (SELECT id FROM position WHERE name='Crucifix'              AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Bread Cutter Choke',         'SUBMISSION', 'Estrangulación desde half guard top cortando el cuello con el antebrazo. Gi. Muy eficaz con crossface.',
    'BLUE',   'GI',   (SELECT id FROM position WHERE name='Half Guard'            AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Mounted Triangle',           'SUBMISSION', 'Triángulo desde mount: piernas alrededor del cuello y brazo del oponente. Muy difícil de escapar para el oponente.',
    'PURPLE', 'BOTH', (SELECT id FROM position WHERE name='Full Mount'            AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Reverse Triangle',           'SUBMISSION', 'Triángulo invertido con piernas alrededor del cuello en posición opuesta. Desde north-south o turtle top.',
    'PURPLE', 'BOTH', (SELECT id FROM position WHERE name='North South'           AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Arm Crush',                  'SUBMISSION', 'Compresión del brazo atrapado desde mount usando el peso del cuerpo. Variante de keylock sin grip en la mano.',
    'WHITE',  'BOTH', (SELECT id FROM position WHERE name='Full Mount'            AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

-- Arm locks adicionales
(1, 'Monoplata',                  'SUBMISSION', 'Omoplata invertida con el oponente en posición opuesta. Difícil de configurar pero muy inesperada.',
    'BROWN',  'BOTH', (SELECT id FROM position WHERE name='Side Control'          AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Armbar from Mount',          'SUBMISSION', 'Armbar clásico desde mount: levantar la cadera, rotar sobre el brazo y extender las caderas. El más fundamental.',
    'WHITE',  'BOTH', (SELECT id FROM position WHERE name='Full Mount'            AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Spinning Armbar',            'SUBMISSION', 'Armbar rotacional desde cualquier posición haciendo una vuelta completa sobre el brazo del oponente.',
    'PURPLE', 'BOTH', (SELECT id FROM position WHERE name='Side Control'          AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Shoulder Lock',              'SUBMISSION', 'Llave de hombro sin figura de 4, sólo usando el peso y la torsión directa. Variante simple pero efectiva.',
    'WHITE',  'BOTH', (SELECT id FROM position WHERE name='Side Control'          AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Kimura from Guard',          'SUBMISSION', 'Kimura atacada desde guardia cerrada o mariposa. El grip de kimura desde abajo también es herramienta de control.',
    'WHITE',  'BOTH', (SELECT id FROM position WHERE name='Closed Guard'          AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Kimura from Back',           'SUBMISSION', 'Kimura atacada desde back mount cuando los ganchos están en su lugar. Alta tasa de finalización.',
    'BLUE',   'BOTH', (SELECT id FROM position WHERE name='Back Mount'            AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Hammerlock',                 'SUBMISSION', 'Llave de hombro empujando la mano hacia la espalda (chicken wing). Desde back mount o crucifix.',
    'PURPLE', 'BOTH', (SELECT id FROM position WHERE name='Back Mount'            AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

-- Leg locks adicionales
(1, 'Ankle Lock from 50/50',      'SUBMISSION', 'Llave de tobillo desde posición 50/50. Atacar mientras el oponente también ataca crea urgencia táctica.',
    'BLUE',   'BOTH', (SELECT id FROM position WHERE name='50/50'                 AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Heel Hook from Saddle',      'SUBMISSION', 'Inside heel hook desde la posición saddle/honey hole. El ataque más poderoso y eficiente del leg lock game moderno.',
    'BROWN',  'NOGI', (SELECT id FROM position WHERE name='Saddle'                AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Outside Heel Hook 50/50',    'SUBMISSION', 'Outside heel hook desde 50/50. Ataca el ligamento cruzado. Tácticamente más arriesgado que el inside.',
    'BROWN',  'NOGI', (SELECT id FROM position WHERE name='50/50'                 AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Heel Hook from Double Outside', 'SUBMISSION', 'Heel hook desde doble ashi exterior. Alto control de la pierna. Sistema avanzado del grappling moderno.',
    'BROWN',  'NOGI', (SELECT id FROM position WHERE name='Double Outside Ashi'   AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Compression Lock',           'SUBMISSION', 'Compresión muscular usando el antebrazo en el hueco del codo o rodilla. Ataca el tejido blando.',
    'PURPLE', 'BOTH', (SELECT id FROM position WHERE name='Leg Entanglement'      AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Hip Lock',                   'SUBMISSION', 'Llave de cadera torciendo la articulación de la cadera. Desde top side control o mount. Alta exigencia técnica.',
    'BROWN',  'BOTH', (SELECT id FROM position WHERE name='Side Control'          AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Toehold from Half Guard',    'SUBMISSION', 'Toe hold atacado desde half guard cuando el oponente intenta pasar. Sorpresivo y de alta efectividad.',
    'PURPLE', 'BOTH', (SELECT id FROM position WHERE name='Half Guard'            AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Kneebar from Top',           'SUBMISSION', 'Kneebar desde posición top cuando el oponente abre las piernas. Rotación sobre la pierna y extensión de caderas.',
    'PURPLE', 'BOTH', (SELECT id FROM position WHERE name='Leg Knot'              AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Neck Crank',                 'SUBMISSION', 'Manipulación cervical forzando la flexión o rotación del cuello. Técnica avanzada, ilegal en muchas competiciones gi.',
    'BROWN',  'NOGI', (SELECT id FROM position WHERE name='Front Headlock'        AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Truck Roll Choke',           'SUBMISSION', 'Estrangulación rodando desde la posición truck de 10th Planet. Crotch control más RNC.',
    'BROWN',  'NOGI', (SELECT id FROM position WHERE name='Truck'                 AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Darce from Turtle',          'SUBMISSION', 'D''Arce choke desde turtle top: insertar el brazo bajo el cuello mientras el oponente se defiende en tortuga.',
    'BLUE',   'NOGI', (SELECT id FROM position WHERE name='Turtle Top'            AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Buggy Choke',                'SUBMISSION', 'Triángulo con el brazo propio desde bottom side control. Contraintuitivo: sin moverse, atrapar el propio brazo.',
    'BROWN',  'BOTH', (SELECT id FROM position WHERE name='Bottom Side Control'   AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Shoulder Choke',             'SUBMISSION', 'Estrangulación usando el hombro propio empujando contra el cuello del oponente. No-gi. Desde side control pesado.',
    'PURPLE', 'NOGI', (SELECT id FROM position WHERE name='Side Control'          AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Inverted Armbar',            'SUBMISSION', 'Armbar invertido desde guardia o posición invertida atacando el codo en dirección opuesta al armbar estándar.',
    'PURPLE', 'BOTH', (SELECT id FROM position WHERE name='Inverted Guard'        AND owner_id=1), (SELECT id FROM position WHERE name='Submitted' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

-- =====================================================================
-- SWEEPS adicionales (~20)
-- =====================================================================

(1, 'Overhead Sweep',             'SWEEP', 'Barrido por encima de la cabeza desde guardia cerrada. El oponente cargar su peso hacia adelante y se lanza por encima.',
    'WHITE',  'BOTH', (SELECT id FROM position WHERE name='Closed Guard'          AND owner_id=1), (SELECT id FROM position WHERE name='Side Control'  AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Pendulum Sweep',             'SWEEP', 'Barrido pendular desde closed guard. Pierna libre oscila para desequilibrar. También llamado Tomoe Nage desde guardia.',
    'WHITE',  'BOTH', (SELECT id FROM position WHERE name='Closed Guard'          AND owner_id=1), (SELECT id FROM position WHERE name='Full Mount'    AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Double Ankle Sweep',         'SWEEP', 'Control de ambos tobillos desde seated guard empujando con los pies en las caderas para derribar.',
    'WHITE',  'BOTH', (SELECT id FROM position WHERE name='Seated Guard'          AND owner_id=1), (SELECT id FROM position WHERE name='Side Control'  AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Collar Drag Sweep',          'SWEEP', 'Arrastre del cuello desde guardia usando el grip en el cuello. Desequilibra al oponente hacia el costado.',
    'WHITE',  'BOTH', (SELECT id FROM position WHERE name='Closed Guard'          AND owner_id=1), (SELECT id FROM position WHERE name='Side Control'  AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Technical Stand Sweep',      'SWEEP', 'Levantarse técnicamente desde single leg X o SLX para voltear al oponente de pie o tomar espalda.',
    'BLUE',   'BOTH', (SELECT id FROM position WHERE name='Single Leg X'          AND owner_id=1), (SELECT id FROM position WHERE name='Side Control'  AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Shin-to-Shin Sweep',         'SWEEP', 'Barrido desde shin-on-shin guard usando el gancho del pie para derribar al oponente de espaldas.',
    'BLUE',   'BOTH', (SELECT id FROM position WHERE name='Shin-on-Shin Guard'    AND owner_id=1), (SELECT id FROM position WHERE name='Side Control'  AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Reverse DLR Sweep',          'SWEEP', 'Barrido desde reverse De La Riva: arrastre del talón y empuje de cadera para tirar al oponente.',
    'BLUE',   'BOTH', (SELECT id FROM position WHERE name='Reverse De La Riva'    AND owner_id=1), (SELECT id FROM position WHERE name='Side Control'  AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'K-Guard Sweep',              'SWEEP', 'Barrido desde K-Guard usando el gancho exterior y el control del tobillo para tirar al oponente lateralmente.',
    'BLUE',   'BOTH', (SELECT id FROM position WHERE name='K-Guard'               AND owner_id=1), (SELECT id FROM position WHERE name='Side Control'  AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Collar Sleeve Sweep',        'SWEEP', 'Barrido desde collar-sleeve guard: jalar la manga hacia abajo mientras se empuja con el pie en la cadera.',
    'BLUE',   'GI',   (SELECT id FROM position WHERE name='Collar Sleeve Guard'   AND owner_id=1), (SELECT id FROM position WHERE name='Full Mount'    AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Tomoe Nage',                 'SWEEP', 'Proyección de sacrificio de judo: caer de espaldas y lanzar al oponente por encima usando los pies en el abdomen.',
    'BLUE',   'BOTH', (SELECT id FROM position WHERE name='Closed Guard'          AND owner_id=1), (SELECT id FROM position WHERE name='Full Mount'    AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Koga Sweep',                 'SWEEP', 'Barrido desde butterfly con underhook: explotar hacia el lado del underhook rodando para tomar espalda o mount.',
    'BLUE',   'BOTH', (SELECT id FROM position WHERE name='Butterfly Guard'       AND owner_id=1), (SELECT id FROM position WHERE name='Back Mount'    AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Half Guard Back Sweep',      'SWEEP', 'Desde half guard profunda, rodar hacia atrás usando el underhook para tomar la espalda o llegar a turtle top.',
    'BLUE',   'BOTH', (SELECT id FROM position WHERE name='Half Guard'            AND owner_id=1), (SELECT id FROM position WHERE name='Back Mount'    AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Worm Guard Sweep',           'SWEEP', 'Barrido usando el sistema worm guard: solapa enroscada al brazo del oponente para desequilibrarlo.',
    'PURPLE', 'GI',   (SELECT id FROM position WHERE name='Worm Guard'            AND owner_id=1), (SELECT id FROM position WHERE name='Full Mount'    AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Hook Sweep',                 'SWEEP', 'Barrido desde butterfly o media guardia usando el gancho del pie interior para elevar y voltear.',
    'WHITE',  'BOTH', (SELECT id FROM position WHERE name='Butterfly Guard'       AND owner_id=1), (SELECT id FROM position WHERE name='Side Control'  AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Lasso Back Take Sweep',      'SWEEP', 'Desde lasso guard, rodar para tomar la espalda cuando el oponente intenta liberarse del enredo de brazo.',
    'PURPLE', 'GI',   (SELECT id FROM position WHERE name='Lasso Guard'           AND owner_id=1), (SELECT id FROM position WHERE name='Back Mount'    AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Stand Up Sweep',             'SWEEP', 'Levantarse de guardia cerrada a single leg: desequilibrar al oponente y tomar single leg desde posición de guardia.',
    'WHITE',  'BOTH', (SELECT id FROM position WHERE name='Closed Guard'          AND owner_id=1), (SELECT id FROM position WHERE name='Side Control'  AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Deep Half Back Take',        'SWEEP', 'Desde deep half guard rotar 180° para tomar la espalda del oponente. Transición fluida de abajo a arriba.',
    'PURPLE', 'BOTH', (SELECT id FROM position WHERE name='Deep Half Guard'       AND owner_id=1), (SELECT id FROM position WHERE name='Back Mount'    AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'X-Guard Back Sweep',         'SWEEP', 'Desde X-guard arrastrar al oponente hacia atrás cayendo de espaldas. Barrido de gran eficiencia en competición.',
    'BLUE',   'BOTH', (SELECT id FROM position WHERE name='X-Guard'               AND owner_id=1), (SELECT id FROM position WHERE name='Full Mount'    AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Dogfight Back Take',         'SWEEP', 'Desde dogfight: cuando ambos compiten underhooks, tirar el brazo lejano para tomar la espalda.',
    'BLUE',   'BOTH', (SELECT id FROM position WHERE name='Dogfight'              AND owner_id=1), (SELECT id FROM position WHERE name='Back Mount'    AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Spider Tomo Sweep',          'SWEEP', 'Barrido de sacrificio desde spider guard: jalar bíceps y empujar con pies para proyectar hacia atrás al oponente.',
    'BLUE',   'GI',   (SELECT id FROM position WHERE name='Spider Guard'          AND owner_id=1), (SELECT id FROM position WHERE name='Full Mount'    AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

-- =====================================================================
-- PASSES adicionales (~18)
-- =====================================================================

(1, 'Knee Cut to Mount',          'PASS', 'Variante del knee slice que termina directamente en mount aprovechando el momentum del corte de rodilla.',
    'BLUE',   'BOTH', (SELECT id FROM position WHERE name='Half Guard'            AND owner_id=1), (SELECT id FROM position WHERE name='Full Mount'   AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Pressure Pass',              'PASS', 'Pase de presión máxima usando el peso corporal para aplanar al oponente y lentamente pasar la guardia.',
    'BLUE',   'BOTH', (SELECT id FROM position WHERE name='Closed Guard'          AND owner_id=1), (SELECT id FROM position WHERE name='Side Control' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Folding Pass',               'PASS', 'Doblar las piernas del oponente sobre su propio cuerpo para pasar. Stack y presión en mismo movimiento.',
    'BLUE',   'BOTH', (SELECT id FROM position WHERE name='Closed Guard'          AND owner_id=1), (SELECT id FROM position WHERE name='Side Control' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Headquarters Pass',          'PASS', 'Posición entre half guard y side control: una rodilla en el suelo, otra fuera. Controla antes de decidir la dirección del pase.',
    'BLUE',   'BOTH', (SELECT id FROM position WHERE name='Half Guard'            AND owner_id=1), (SELECT id FROM position WHERE name='Side Control' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Roll Under Pass',            'PASS', 'Rodar bajo las piernas del oponente para aparecer al otro lado. Contra spider guard y guardias cerradas de pies altos.',
    'PURPLE', 'GI',   (SELECT id FROM position WHERE name='Spider Guard'          AND owner_id=1), (SELECT id FROM position WHERE name='Side Control' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Stack Pass',                 'PASS', 'Apilar al oponente sobre sus propios hombros. Presión cervical. Se combina con double under para pasar.',
    'WHITE',  'BOTH', (SELECT id FROM position WHERE name='Closed Guard'          AND owner_id=1), (SELECT id FROM position WHERE name='Side Control' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Toreando 3/4 Pass',          'PASS', 'Torreando incompleto que deja una rodilla al suelo: posición de headquarters para controlar medio camino del pase.',
    'BLUE',   'BOTH', (SELECT id FROM position WHERE name='Seated Guard'          AND owner_id=1), (SELECT id FROM position WHERE name='Half Guard'   AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Reverse Half Pass',          'PASS', 'Pasar la media guardia del lado inverso: en lugar de cruzar, dar la vuelta por detrás del oponente.',
    'PURPLE', 'BOTH', (SELECT id FROM position WHERE name='Half Guard'            AND owner_id=1), (SELECT id FROM position WHERE name='Back Mount'   AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Leg Drag to Back',           'PASS', 'Leg drag que convierte el pase en toma de espalda cuando el oponente gira para seguir el arrastre de pierna.',
    'BLUE',   'BOTH', (SELECT id FROM position WHERE name='De La Riva Guard'      AND owner_id=1), (SELECT id FROM position WHERE name='Back Mount'   AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Forced Half Pass',           'PASS', 'Forzar al oponente a media guardia desde guardia cerrada y luego terminar el pase desde ahí. Flujo estructurado.',
    'WHITE',  'BOTH', (SELECT id FROM position WHERE name='Closed Guard'          AND owner_id=1), (SELECT id FROM position WHERE name='Side Control' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Lasso Guard Pass',           'PASS', 'Pasar la lasso guard: liberar el brazo enroscado, controlar la cadera y pasar rápido antes de que recupere el control.',
    'PURPLE', 'GI',   (SELECT id FROM position WHERE name='Lasso Guard'           AND owner_id=1), (SELECT id FROM position WHERE name='Side Control' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Spider Guard Pass',          'PASS', 'Pasar la spider guard: bajar los pies de los bíceps, controlar las rodillas, torreando o knee cut.',
    'BLUE',   'GI',   (SELECT id FROM position WHERE name='Spider Guard'          AND owner_id=1), (SELECT id FROM position WHERE name='Side Control' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Hip Escape Pass Counter',    'PASS', 'Seguir el shrimp del oponente desde side control para avanzar a mount o norte-sur sin perder el control.',
    'BLUE',   'BOTH', (SELECT id FROM position WHERE name='Bottom Side Control'   AND owner_id=1), (SELECT id FROM position WHERE name='Full Mount'   AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Knee Tap Pass',              'PASS', 'Mover la rodilla del oponente a un lado para abrir paso al lado contrario. Velocidad y timing críticos.',
    'WHITE',  'BOTH', (SELECT id FROM position WHERE name='Seated Guard'          AND owner_id=1), (SELECT id FROM position WHERE name='Side Control' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Drive Through Pass',         'PASS', 'Empujar a través del centro de la guardia con cadera baja. No-gi. Contra guardias abiertas ligeras.',
    'BLUE',   'NOGI', (SELECT id FROM position WHERE name='Butterfly Guard'       AND owner_id=1), (SELECT id FROM position WHERE name='Side Control' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Cartwheel Pass',             'PASS', 'Pase acrobático voltereta lateral sobre las piernas del oponente. Gi y no-gi. Alta recompensa, requiere atletismo.',
    'PURPLE', 'BOTH', (SELECT id FROM position WHERE name='De La Riva Guard'      AND owner_id=1), (SELECT id FROM position WHERE name='Side Control' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Esgrima Pass',               'PASS', 'Pase de esgrima: un brazo controla la rodilla, el otro empuja la cadera, paso lateral rápido. Sin gi.',
    'BLUE',   'NOGI', (SELECT id FROM position WHERE name='Seated Guard'          AND owner_id=1), (SELECT id FROM position WHERE name='Side Control' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Half Guard Top Pass',        'PASS', 'Completar el pase desde half guard top usando underpinning: hundirse bajo el underhook y rodar a mount.',
    'BLUE',   'BOTH', (SELECT id FROM position WHERE name='Half Guard'            AND owner_id=1), (SELECT id FROM position WHERE name='Full Mount'   AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

-- =====================================================================
-- TAKEDOWNS adicionales (~10)
-- =====================================================================

(1, 'Fireman Carry',              'TAKEDOWN', 'Proyección de bombero: control del brazo + pierna, volteo frontal. Gi y no-gi. Alta efectividad desde clinch.',
    'BLUE',   'BOTH', (SELECT id FROM position WHERE name='Fireman Carry Position' AND owner_id=1), (SELECT id FROM position WHERE name='Side Control' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Suplex',                     'TAKEDOWN', 'Proyección hacia atrás elevando al oponente. Gi y no-gi. Muy espectacular pero requiere fuerza y timing.',
    'BLUE',   'BOTH', (SELECT id FROM position WHERE name='Rear Standing'         AND owner_id=1), (SELECT id FROM position WHERE name='Side Control' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Inside Trip',                'TAKEDOWN', 'Viaje de pie interior desde clinch: pierna barre la parte interna de la rodilla del oponente.',
    'WHITE',  'BOTH', (SELECT id FROM position WHERE name='Clinch'               AND owner_id=1), (SELECT id FROM position WHERE name='Side Control' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Outside Trip',               'TAKEDOWN', 'Viaje de pie exterior: pierna barre la parte externa de la rodilla. Judo (Osoto-gari). Muy común en BJJ.',
    'WHITE',  'BOTH', (SELECT id FROM position WHERE name='Clinch'               AND owner_id=1), (SELECT id FROM position WHERE name='Side Control' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'High Crotch',                'TAKEDOWN', 'Variante del single leg con control más alto en la ingle. Transición a single leg takedown o double leg.',
    'WHITE',  'BOTH', (SELECT id FROM position WHERE name='Single Leg'           AND owner_id=1), (SELECT id FROM position WHERE name='Side Control' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Knee Pick',                  'TAKEDOWN', 'Agarre de la rodilla del oponente jalándola hacia arriba mientras se empuja el cuerpo. Bajo riesgo.',
    'WHITE',  'BOTH', (SELECT id FROM position WHERE name='Clinch'               AND owner_id=1), (SELECT id FROM position WHERE name='Side Control' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Duck Under',                 'TAKEDOWN', 'Duckar bajo el brazo del oponente para tomar la espalda o ejecutar derribo. Desde collar tie o underhook.',
    'BLUE',   'BOTH', (SELECT id FROM position WHERE name='Collar Tie'           AND owner_id=1), (SELECT id FROM position WHERE name='Back Mount'   AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Blast Double Leg',           'TAKEDOWN', 'Double leg explosivo sin pasos intermedios, directo desde neutral. Alta velocidad de entrada.',
    'BLUE',   'NOGI', (SELECT id FROM position WHERE name='Standing Neutral'     AND owner_id=1), (SELECT id FROM position WHERE name='Side Control' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Uchi Mata',                  'TAKEDOWN', 'Proyección de judo con el muslo interior. Alta efectividad en competición de gi. Requiere buen kuzushi.',
    'BLUE',   'GI',   (SELECT id FROM position WHERE name='Clinch'               AND owner_id=1), (SELECT id FROM position WHERE name='Side Control' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Seoi Otoshi',                'TAKEDOWN', 'Caída de espalda hacia adelante cargando al oponente sobre el hombro sin la rotación completa del Seoi Nage.',
    'BLUE',   'GI',   (SELECT id FROM position WHERE name='Clinch'               AND owner_id=1), (SELECT id FROM position WHERE name='Side Control' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

-- =====================================================================
-- ESCAPES adicionales (~10)
-- =====================================================================

(1, 'Shrimp to Guard',            'ESCAPE', 'Hip escape (gambas) desde bottom side control para crear espacio e insertar la rodilla y recuperar guardia.',
    'WHITE',  'BOTH', (SELECT id FROM position WHERE name='Bottom Side Control'  AND owner_id=1), (SELECT id FROM position WHERE name='Closed Guard' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Granby Roll',                'ESCAPE', 'Rodillo de hombro invertido para escapar de pressure passing o turtle. Recuperar guardia rodando por encima.',
    'BLUE',   'BOTH', (SELECT id FROM position WHERE name='Turtle'               AND owner_id=1), (SELECT id FROM position WHERE name='Inverted Guard' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Mount Escape to Deep Half',  'ESCAPE', 'Desde bottom mount, hundir la cadera y rodarse al lado para entrar a deep half guard en lugar de half guard estándar.',
    'BLUE',   'BOTH', (SELECT id FROM position WHERE name='Bottom Mount'         AND owner_id=1), (SELECT id FROM position WHERE name='Deep Half Guard' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Leg Pummeling',              'ESCAPE', 'Pelea de piernas para reconfigurar el leg entanglement: reposicionar la pierna atrapada para salir o mejorar.',
    'PURPLE', 'NOGI', (SELECT id FROM position WHERE name='Leg Entanglement'     AND owner_id=1), (SELECT id FROM position WHERE name='Seated Guard'   AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Standing Up from Bottom',    'ESCAPE', 'Levantarse técnicamente desde bottom guard: dos manos al suelo, pie libre adelante, explosión hacia arriba.',
    'WHITE',  'BOTH', (SELECT id FROM position WHERE name='Closed Guard'         AND owner_id=1), (SELECT id FROM position WHERE name='Standing Neutral' AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Scarf Hold Escape',          'ESCAPE', 'Escape de kesa gatame: puente y rodillo o insertar la rodilla para girar y recuperar guardia.',
    'WHITE',  'BOTH', (SELECT id FROM position WHERE name='Scarf Hold'           AND owner_id=1), (SELECT id FROM position WHERE name='Half Guard'     AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Back Defense Hip Escape',    'ESCAPE', 'Defensa de espalda usando hip escape lateral: deslizarse por debajo de un gancho para liberar la posición.',
    'BLUE',   'BOTH', (SELECT id FROM position WHERE name='Bottom Back Mount'    AND owner_id=1), (SELECT id FROM position WHERE name='Half Guard'     AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Turtle Granby',              'ESCAPE', 'Desde turtle, rodar con el hombro en arco para reconfigurar y recuperar guardia o llegar a posición neutral.',
    'BLUE',   'BOTH', (SELECT id FROM position WHERE name='Turtle'               AND owner_id=1), (SELECT id FROM position WHERE name='Half Guard'     AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Choke Defense',              'ESCAPE', 'Defensa de estrangulaciones: tucking the chin, gripper''s elbows, crear espacio para el cuello y escapar.',
    'WHITE',  'BOTH', (SELECT id FROM position WHERE name='Bottom Back Mount'    AND owner_id=1), (SELECT id FROM position WHERE name='Side Control'   AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'North-South Back Roll',      'ESCAPE', 'Desde north-south invertido, rodar hacia atrás sobre los hombros para recuperar guardia dándole la vuelta.',
    'BLUE',   'BOTH', (SELECT id FROM position WHERE name='Bottom Side Control'  AND owner_id=1), (SELECT id FROM position WHERE name='Closed Guard'   AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

-- =====================================================================
-- TRANSITIONS adicionales (~14)
-- =====================================================================

(1, 'Side Control to Crucifix',   'TRANSITION', 'Desde side control cuando el oponente inserta un brazo para escapar, atrapar ese brazo con las piernas.',
    'BLUE',   'BOTH', (SELECT id FROM position WHERE name='Side Control'         AND owner_id=1), (SELECT id FROM position WHERE name='Crucifix'              AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Knee On Belly to Armbar',    'TRANSITION', 'Desde knee on belly: cuando el oponente empuja la rodilla, rotar hacia el armbar aprovechando el brazo extendido.',
    'BLUE',   'BOTH', (SELECT id FROM position WHERE name='Knee On Belly'        AND owner_id=1), (SELECT id FROM position WHERE name='Submitted'             AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Turtle to Crucifix',         'TRANSITION', 'Desde turtle top: insertar la pierna dentro de las piernas del oponente para entrar a la posición crucifix.',
    'PURPLE', 'BOTH', (SELECT id FROM position WHERE name='Turtle Top'           AND owner_id=1), (SELECT id FROM position WHERE name='Crucifix'              AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Guard to Turtle',            'TRANSITION', 'Rodar de guardia a turtle cuando el oponente pasa agresivamente. Defensive reset.',
    'WHITE',  'BOTH', (SELECT id FROM position WHERE name='Closed Guard'         AND owner_id=1), (SELECT id FROM position WHERE name='Turtle'                AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'DLR to Berimbolo',           'TRANSITION', 'Entrar al berimbolo desde DLR cuando el oponente intenta pasar. Inversión completa bajo el oponente.',
    'PURPLE', 'GI',   (SELECT id FROM position WHERE name='De La Riva Guard'     AND owner_id=1), (SELECT id FROM position WHERE name='Back Mount'             AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Mount to Triangle',          'TRANSITION', 'Configurar el triángulo desde mount cuando el oponente extiende los brazos para escapar el mount.',
    'BLUE',   'BOTH', (SELECT id FROM position WHERE name='Full Mount'           AND owner_id=1), (SELECT id FROM position WHERE name='Closed Guard'           AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Side Control to Leg Lock',   'TRANSITION', 'Desde side control top: pivotar hacia las piernas del oponente para entrar a ashi garami o saddle.',
    'PURPLE', 'NOGI', (SELECT id FROM position WHERE name='Side Control'         AND owner_id=1), (SELECT id FROM position WHERE name='Saddle'                 AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Closed Guard to Spider',     'TRANSITION', 'Abrir la guardia y reconfigurar los pies a bíceps del oponente para pasar de closed a spider guard.',
    'BLUE',   'GI',   (SELECT id FROM position WHERE name='Closed Guard'         AND owner_id=1), (SELECT id FROM position WHERE name='Spider Guard'           AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Closed Guard to DLR',        'TRANSITION', 'Transición de guardia cerrada a De La Riva cuando el oponente se levanta a base neutral.',
    'BLUE',   'GI',   (SELECT id FROM position WHERE name='Closed Guard'         AND owner_id=1), (SELECT id FROM position WHERE name='De La Riva Guard'       AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Half Guard to Z-Guard',      'TRANSITION', 'Desde half guard insertar la rodilla como escudo para crear la posición de Z-guard con más control de distancia.',
    'WHITE',  'BOTH', (SELECT id FROM position WHERE name='Half Guard'           AND owner_id=1), (SELECT id FROM position WHERE name='Z-Guard'                AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'SLX to Saddle',              'TRANSITION', 'Reconfigurar las piernas de single leg X a saddle/honey hole para mejorar el ángulo del heel hook.',
    'PURPLE', 'NOGI', (SELECT id FROM position WHERE name='Single Leg X'         AND owner_id=1), (SELECT id FROM position WHERE name='Saddle'                 AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Back to Mount',              'TRANSITION', 'Desde back mount: cuando se pierde un gancho, pivotar hacia adelante para llegar a mount alto.',
    'WHITE',  'BOTH', (SELECT id FROM position WHERE name='Back Mount'           AND owner_id=1), (SELECT id FROM position WHERE name='High Mount'             AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Turtle to Leg Lock Entry',   'TRANSITION', 'Desde turtle top: pasar la pierna por encima para entrar directamente a ashi garami o saddle.',
    'PURPLE', 'NOGI', (SELECT id FROM position WHERE name='Turtle Top'           AND owner_id=1), (SELECT id FROM position WHERE name='Saddle'                 AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Butterfly to X-Guard',       'TRANSITION', 'Desde butterfly guard: reconfigurar los ganchos para llegar a X-guard cuando el oponente se levanta.',
    'BLUE',   'BOTH', (SELECT id FROM position WHERE name='Butterfly Guard'      AND owner_id=1), (SELECT id FROM position WHERE name='X-Guard'                AND owner_id=1), 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);
