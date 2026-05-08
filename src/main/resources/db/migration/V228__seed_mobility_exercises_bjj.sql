-- V228__seed_mobility_exercises_bjj.sql
-- 25 ejercicios MOBILITY + 20 ejercicios FLEXIBILITY específicos para BJJ (owner_id=1).
-- Requiere V215 (tabla exercise) ejecutado primero.

INSERT INTO exercise (owner_id, name, description, category, equipment, youtube_url, visibility, created_at, updated_at, version) VALUES

-- =====================================================================
-- MOBILITY — BJJ-specific drills (25)
-- =====================================================================

(1, 'Shrimp (Camarón)',
 'Movimiento de cadera fundamental del BJJ para recuperar guardia. Empuja con los pies y escapa la cadera lateralmente tumbado en el suelo.',
 'MOBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Granby Roll',
 'Voltereta lateral sobre el hombro, esencial para escapar de posiciones de control como el turtle o la presión de side control.',
 'MOBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Technical Stand-Up',
 'Levantarse del suelo guardando distancia y base. Técnica fundamental del BJJ de pie para recuperarse sin exponer la espalda.',
 'MOBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Hip Escape Drill',
 'Encadenado de camarones continuos para trabajar la movilidad de cadera en el suelo. Pilar del BJJ defensivo.',
 'MOBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Sit-Out',
 'Transición explosiva de cuatro apoyos a posición lateral. Esencial para escapar del control de wrestling y crear ángulos.',
 'MOBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Stand-Up in Base',
 'Levantarse manteniendo base estable contra derribos. Trabaja el equilibrio y la coordinación en el paso de suelo a pie.',
 'MOBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Penetration Step',
 'Paso de penetración hacia adentro para entrar en single leg y double leg. Entrenado como movimiento de movilidad aislado.',
 'MOBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Armbar Hip Extension Drill',
 'Extensión de cadera explosiva desde posición de armbar en el suelo. Mejora el finishing del armbar y la movilidad lumbar.',
 'MOBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Guard Recovery Hip Circle',
 'Círculos de cadera en el suelo para recuperar guardia cuando el rival intenta pasar. Trabaja amplitud y velocidad de cadera.',
 'MOBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Bridge (Puente)',
 'Puente de cadera explosivo desde el suelo. Base del escape de mount y ejercicio clave de calentamiento lumbar y glúteo.',
 'MOBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Bridge and Roll Drill',
 'Encadenado de puentes laterales para practicar el mecanismo completo de escape de mount. Combina explosividad y coordinación.',
 'MOBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Neck Mobility Circles',
 'Círculos lentos y controlados de cuello para proteger la columna cervical en el tatami. Imprescindible antes y después de rodar.',
 'MOBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Shoulder Roll Forward',
 'Rodada hacia adelante sobre el hombro para caídas seguras (ukemi) y transiciones fluidas en el suelo.',
 'MOBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Shoulder Roll Backward',
 'Rodada hacia atrás sobre el hombro, fundamental para ukemi en BJJ y escapar de presiones hacia atrás.',
 'MOBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Sprawl Drill',
 'Sprawl repetido para defensa de derribos. Trabaja la reacción de cadera y la extensión lumbar para neutralizar single y double legs.',
 'MOBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Wrestler''s Sit-Out Series',
 'Serie de sit-outs encadenados de wrestling. Mejora la movilidad lateral y las transiciones rápidas cuando el rival tiene control de cintura.',
 'MOBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Duck Walk',
 'Caminar en cuclillas profundas hacia adelante y atrás. Mejora la movilidad de cadera, rodilla y tobillo esencial para guard players.',
 'MOBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Crab Walk',
 'Caminar como cangrejo con el cuerpo elevado del suelo. Trabaja movilidad de hombros, cadera y coordinación para posiciones de turtle.',
 'MOBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Inchworm',
 'Gusano: desde de pie, llevar las manos al suelo y avanzar hasta plancha, luego volver. Combina movilidad de isquiotibiales y tren superior.',
 'MOBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Jiu-Jitsu Hip Mobility Flow',
 'Flujo completo de movilidad de cadera encadenando shrimp + bridge + guard recovery roll. Calentamiento completo pre-sesión BJJ.',
 'MOBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Knee Circle Drill',
 'Círculos de rodilla en postura de guardia para calentar la articulación antes de rodar. Previene lesiones en guard players.',
 'MOBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Ankle Rotation Drill',
 'Rotaciones de tobillo en todas las direcciones. Clave para guard players que trabajan leg locks y necesitan movilidad articular.',
 'MOBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Wrist Mobility Drill',
 'Movilidad y calentamiento de muñecas con círculos y extensiones. Imprescindible para wrestling, pummeling y clinch work.',
 'MOBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Thoracic Spine Rotation',
 'Rotación de columna torácica en posición de cuatro apoyos o sentado. Mejora la capacidad de girar para guardias y pasajes.',
 'MOBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Hip Flexor Lunge Flow',
 'Estocadas con flujo de cadera hacia adelante y atrás, añadiendo rotación. Abre el flexor de cadera para mejorar la guardia cerrada y abierta.',
 'MOBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

-- =====================================================================
-- FLEXIBILITY — BJJ-specific stretches (20)
-- =====================================================================

(1, 'Butterfly Stretch',
 'Estiramiento de aductores en posición de guardia mariposa: plantas de los pies juntas, rodillas hacia el suelo. Mejora la apertura de cadera.',
 'FLEXIBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Hip Flexor Stretch',
 'Apertura del flexor de cadera en posición de estocada baja. Mejora la guardia cerrada y la postura en el suelo.',
 'FLEXIBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Seated Forward Fold',
 'Flexión hacia adelante sentado con piernas extendidas. Estira isquiotibiales y zona lumbar, fundamental para guard play.',
 'FLEXIBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Spinal Twist Stretch',
 'Torsión espinal sentado cruzando una pierna. Mejora la movilidad de columna para barridas, guardias y transiciones rotacionales.',
 'FLEXIBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Pigeon Pose',
 'Apertura profunda de cadera en posición de paloma (yoga). Mejora significativamente la guardia y facilita los leg locks.',
 'FLEXIBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Frog Stretch',
 'Estiramiento de rana: rodillas separadas en el suelo, cadera hacia atrás. Para aductores profundos, essential en half guard y De La Riva.',
 'FLEXIBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Standing Hip Circle',
 'Círculos de cadera de pie con amplitud máxima. Calentamiento dinámico antes de rodar para activar toda la articulación de cadera.',
 'FLEXIBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Shoulder Cross-Body Stretch',
 'Estiramiento de hombro cruzando el brazo ante el pecho. Previene lesiones de kimura y americana trabajando el manguito rotador.',
 'FLEXIBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Neck Lateral Stretch',
 'Estiramiento lateral de cuello inclinando la cabeza hacia el hombro. Previene lesiones en guillotinas, darce y front headlock.',
 'FLEXIBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Thoracic Extension Stretch',
 'Extensión torácica tumbado en el suelo o sobre un rodillo. Abre el pecho y mejora la postura en guardia, contrarrestando la cifosis del BJJ.',
 'FLEXIBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Couch Stretch',
 'Estiramiento profundo del cuádriceps y flexor de cadera con el pie elevado en la pared. Uno de los más importantes para guard players.',
 'FLEXIBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Pancake Stretch',
 'Apertura de piernas con tronco inclinado hacia el suelo. Estiramiento avanzado para X-guard, leg entanglement y rubber guard.',
 'FLEXIBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Straddle Stretch',
 'Apertura de piernas sentado en el suelo, inclinando el tronco a cada lado. Esencial para De La Riva, rubber guard y guardias abiertas.',
 'FLEXIBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Cobra Pose',
 'Extensión lumbar suave boca abajo apoyándose en las manos. Contrarresta la posición encogida característica del BJJ y estira el abdomen.',
 'FLEXIBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Child''s Pose',
 'Postura del niño: rodillas al suelo, brazos estirados al frente. Relaja la columna, hombros y caderas después del entrenamiento.',
 'FLEXIBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Lat Stretch Hanging',
 'Estiramiento de dorsal colgado de una barra con una o dos manos. Mejora la capacidad de extensión para tomas largas y clinch work.',
 'FLEXIBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Wrist Flexor Stretch',
 'Estiramiento de flexores de muñeca extendiendo el brazo y tirando de los dedos hacia atrás. Previene lesiones en gripping y pummeling.',
 'FLEXIBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Achilles and Calf Stretch',
 'Estiramiento de gemelo y tendón de Aquiles contra la pared. Clave para guard players que mantienen posiciones de rodilla al suelo.',
 'FLEXIBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Glute Stretch Figure-4',
 'Estiramiento de glúteo en figura 4 tumbado o sentado. Mejora la salida de mount, la defensa de back control y el juego De La Riva.',
 'FLEXIBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0),

(1, 'Full Body BJJ Cool-Down Flow',
 'Flujo completo de estiramientos post-entrenamiento BJJ: child''s pose + spinal twist + pigeon + butterfly + forward fold. 10 minutos de recuperación activa.',
 'FLEXIBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', NOW(), NOW(), 0);
