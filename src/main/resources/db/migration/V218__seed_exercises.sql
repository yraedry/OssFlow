-- V218__seed_exercises.sql
-- 90 ejercicios físicos para BJJ y fitness general (owner_id=1).
-- Requiere V215 (tabla exercise) ejecutado primero.
-- Distribuidos: ~32 NO_EQUIPMENT, ~28 HOME, ~30 GYM
-- Categorías: STRENGTH, CARDIO, FLEXIBILITY, CORE, MOBILITY, OTHER

INSERT INTO exercise (owner_id, name, description, category, equipment, youtube_url, visibility, created_at, updated_at, version) VALUES

-- =====================================================================
-- NO_EQUIPMENT — STRENGTH (10)
-- =====================================================================

(1, 'Push-Up',
    'Flexión de brazos estándar. Palmas ligeramente más anchas que los hombros, cuerpo recto, pecho casi tocando el suelo. Trabaja pectoral, tríceps y core.',
    'STRENGTH', 'NO_EQUIPMENT', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Diamond Push-Up',
    'Flexión con las manos formando un diamante bajo el pecho. Aisla el tríceps y la porción interna del pectoral. Esencial para mejorar finalizaciones de armbar.',
    'STRENGTH', 'NO_EQUIPMENT', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Wide Push-Up',
    'Flexión con manos muy abiertas. Mayor énfasis en pectoral externo. Mejora el framing y el control de distancia desde guardia.',
    'STRENGTH', 'NO_EQUIPMENT', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Archer Push-Up',
    'Flexión unilateral: un brazo extendido al lado mientras el otro dobla. Desarrolla fuerza asimétrica y propioception. Progresión hacia push-up con un brazo.',
    'STRENGTH', 'NO_EQUIPMENT', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Pike Push-Up',
    'Flexión en V invertida con las caderas elevadas. Carga el hombro como un press vertical. Preparación para el handstand push-up.',
    'STRENGTH', 'NO_EQUIPMENT', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Bodyweight Squat',
    'Sentadilla con el peso corporal. Talones en el suelo, rodillas siguiendo la dirección de los pies, caderas por debajo de las rodillas. Base de toda la fuerza de piernas.',
    'STRENGTH', 'NO_EQUIPMENT', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Jump Squat',
    'Sentadilla pliométrica con salto explosivo al final del movimiento. Desarrolla potencia de piernas para derribos y barridos explosivos.',
    'STRENGTH', 'NO_EQUIPMENT', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Bulgarian Split Squat',
    'Sentadilla búlgara: pie trasero elevado, pie delantero adelantado. Trabaja cuádriceps, glúteo y equilibrio unilateral. Sin material externo si se apoya en silla.',
    'STRENGTH', 'NO_EQUIPMENT', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Pistol Squat',
    'Sentadilla a una pierna. Máxima exigencia unilateral de cuádriceps, glúteo y equilibrio. Requiere meses de progresión. Excelente para prevención de lesiones de rodilla.',
    'STRENGTH', 'NO_EQUIPMENT', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Inverted Row (Table)',
    'Remo invertido bajo una mesa o barra baja. Cuerpo recto, tirar del pecho hacia la superficie. Trabaja dorsales y bíceps sin material de gym.',
    'STRENGTH', 'NO_EQUIPMENT', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

-- =====================================================================
-- NO_EQUIPMENT — CORE (10)
-- =====================================================================

(1, 'Plank',
    'Plancha isométrica en codos y puntas de pie. Mantener el cuerpo completamente recto sin caer las caderas. Activa toda la cadena anterior del tronco.',
    'CORE', 'NO_EQUIPMENT', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Side Plank',
    'Plancha lateral: apoyo en un codo y el borde del pie. Activa oblicuos, cuadrado lumbar y glúteo medio. Imprescindible para la estabilidad lateral en guardia.',
    'CORE', 'NO_EQUIPMENT', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Hollow Body Hold',
    'Decúbito supino: piernas y hombros elevados, zona lumbar pegada al suelo. La posición base de la gimnasia y del core funcional. Clave para guardias activas.',
    'CORE', 'NO_EQUIPMENT', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Dead Bug',
    'Decúbito supino: piernas y brazos opuestos se alargan simultáneamente sin perder el contacto lumbar. Mejora la coordinación y estabilidad profunda del core.',
    'CORE', 'NO_EQUIPMENT', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'V-Up',
    'Desde decúbito supino, elevar piernas y tronco simultáneamente hacia el centro formando una V. Activa recto abdominal y flexores de cadera.',
    'CORE', 'NO_EQUIPMENT', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Leg Raise',
    'Desde decúbito supino, elevar las piernas juntas hasta 90° sin doblarlas. Trabaja la porción inferior del recto abdominal y los flexores de cadera.',
    'CORE', 'NO_EQUIPMENT', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Mountain Climber',
    'Posición de push-up, llevar las rodillas alternadamente al pecho con rapidez. Combina core con cardio. Simula la explosividad del shrimp bajo presión.',
    'CORE', 'NO_EQUIPMENT', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Ab Wheel Rollout (towel variation)',
    'Rollout con una toalla sobre suelo liso: manos en la toalla, rodar hacia adelante y volver. Trabaja el core anterior con enorme rango de movimiento.',
    'CORE', 'NO_EQUIPMENT', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Russian Twist',
    'Sentado en V, torcer el tronco de lado a lado. Sin peso externo ya trabaja los oblicuos. Simula el giro del torso en barridos y técnicas rotacionales.',
    'CORE', 'NO_EQUIPMENT', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Bear Crawl',
    'Cuadrupedia con rodillas a 5 cm del suelo. Avanzar, retroceder o lateral. Activa core, hombros y cadera de forma integrada. Excelente para la coordinación de BJJ.',
    'CORE', 'NO_EQUIPMENT', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

-- =====================================================================
-- NO_EQUIPMENT — CARDIO (7)
-- =====================================================================

(1, 'Burpee',
    'Thrust al suelo, push-up, salto y palmada arriba. El ejercicio de acondicionamiento total más completo para BJJ. Simula la intensidad de los rounds.',
    'CARDIO', 'NO_EQUIPMENT', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Sprint',
    'Carrera a máxima velocidad en distancias cortas (20-50 m). Desarrolla el sistema de fosfágenos y la capacidad de explosiones repetidas en combate.',
    'CARDIO', 'NO_EQUIPMENT', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Shadow Grappling',
    'Practicar técnicas BJJ en el aire: shots, sprawls, barridos y transiciones sin oponente. Mejora la técnica, el ritmo y el acondicionamiento simultáneamente.',
    'CARDIO', 'NO_EQUIPMENT', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Hip Escape Drill',
    'Ejercicio de gambas continuo a lo largo del tatami: bridge + shrimp repetitivo. Acondicionamiento específico del movimiento más importante de la defensa.',
    'CARDIO', 'NO_EQUIPMENT', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Technical Stand Drill',
    'Levantarse técnicamente de guardia repetidas veces: mano trasera al suelo, pie libre adelante, subir a postura. Acondicionamiento y técnica combinados.',
    'CARDIO', 'NO_EQUIPMENT', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'High Knees',
    'Carrera en el sitio con rodillas elevadas al pecho. Alta frecuencia de zancada. Prepara el sistema cardiovascular y los flexores de cadera.',
    'CARDIO', 'NO_EQUIPMENT', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Box Jump (floor level)',
    'Salto desde posición de cuclillas a posición de pie completamente extendida. Sin caja. Desarrolla la potencia del tren inferior y la reactividad.',
    'CARDIO', 'NO_EQUIPMENT', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

-- =====================================================================
-- NO_EQUIPMENT — MOBILITY y FLEXIBILITY (5)
-- =====================================================================

(1, 'Hip Circle',
    'Rotación completa de la cadera en círculos grandes, de pie. Calienta la articulación coxofemoral antes de entrenamiento o competición.',
    'MOBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Spiderman Stretch',
    'Zancada profunda con la mano en el suelo al nivel del pie adelantado, cadera baja. Movilidad de cadera y flexibilidad de psoas. Esencial para guardas abiertas.',
    'FLEXIBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'World Greatest Stretch',
    'Combinación en un movimiento: lunge + rotación de tronco + extensión de cadera. Movilidad global de cadena posterior, torácica y cadera.',
    'MOBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Neck Bridge',
    'Puente sobre la cabeza y los talones. Fortalece y moviliza la columna cervical. Fundamental para la defensa de estrangulaciones y el wrestling.',
    'MOBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Shoulder Dislocate',
    'Rotación completa de hombros con un palo o toalla: pasar de adelante a atrás con los brazos extendidos. Movilidad de hombro para guardia y control de espalda.',
    'MOBILITY', 'NO_EQUIPMENT', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

-- =====================================================================
-- HOME — STRENGTH (10)
-- =====================================================================

(1, 'Pull-Up',
    'Dominada con agarre prono en cualquier barra de puerta o rama. El ejercicio de tren superior más importante para el BJJ. Trabaja dorsales, bíceps y core.',
    'STRENGTH', 'HOME', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Chin-Up',
    'Dominada con agarre supino (manos hacia el cuerpo). Mayor activación del bíceps. Complementa el pull-up para una espalda equilibrada.',
    'STRENGTH', 'HOME', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Resistance Band Row',
    'Remo con banda elástica fijada en una puerta. Simula el pulling de clinch y control de espalda. Progresa aumentando la resistencia de la banda.',
    'STRENGTH', 'HOME', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Resistance Band Pull-Apart',
    'Sosteniendo la banda a altura de pecho, separar los brazos hasta tocar el pecho. Trabaja los retractores escapulares y el manguito rotador posterior.',
    'STRENGTH', 'HOME', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Resistance Band Press',
    'Press de pecho con banda fijada detrás. Trabaja el empuje horizontal. Usar en superserie con remo para equilibrio de empuje/tracción.',
    'STRENGTH', 'HOME', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Glute Bridge',
    'Decúbito supino, rodillas dobladas, empujar las caderas hacia arriba apretando glúteos. Activa glúteos y cadena posterior. Previene lesiones de rodilla y cadera.',
    'STRENGTH', 'HOME', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Single Leg Glute Bridge',
    'Puente de glúteo a una pierna. Mayor demanda unilateral. Corrige desequilibrios entre piernas y fortalece el glúteo para los derribos.',
    'STRENGTH', 'HOME', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Hip Thrust with Chair',
    'Hombros apoyados en una silla, caderas elevándose desde el suelo con potencia. Máxima activación del glúteo mayor. Fundamental para la explosividad de cadera.',
    'STRENGTH', 'HOME', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Step-Up',
    'Subir y bajar un escalón o silla de forma controlada. Cuádriceps, isquios y glúteo unilateral. Trabaja el control excéntrico de la rodilla.',
    'STRENGTH', 'HOME', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Towel Row',
    'Remo usando una toalla alrededor de una puerta o poste. Simula el grip de gi. Trabaja la tracción y el agarre simultáneamente.',
    'STRENGTH', 'HOME', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

-- =====================================================================
-- HOME — CARDIO (8)
-- =====================================================================

(1, 'Jump Rope',
    'Comba de saltar. Uno de los mejores acondicionadores cardiovasculares. Mejora el ritmo, la coordinación y la resistencia aeróbica en sesiones de 10-20 min.',
    'CARDIO', 'HOME', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Double Under',
    'Comba donde la cuerda pasa dos veces por cada salto. Alta intensidad, mejora la explosividad de piernas y la coordinación ritmica.',
    'CARDIO', 'HOME', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Resistance Band Sprint',
    'Sprint estático con banda elástica fijada detrás de la cintura. Trabaja la mecánica de la zancada y la potencia de cadera bajo resistencia.',
    'CARDIO', 'HOME', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Stair Climbing',
    'Subir escaleras repetidamente a paso rápido o corriendo. Excelente cardio de baja impacto sobre las rodillas comparado con correr. Fácil de integrar en casa.',
    'CARDIO', 'HOME', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Towel Drag Drill',
    'Arrastrar una toalla con ropa húmeda por el suelo simulando el peso de un oponente. Trabaja la cadena posterior y el sistema cardiovascular.',
    'CARDIO', 'HOME', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Resistance Band Sprawl Drill',
    'Con banda en caderas: shot a doble pierna y sprawl de vuelta de forma repetida. Simula la explosividad y el acondicionamiento del wrestling.',
    'CARDIO', 'HOME', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Lateral Shuffle',
    'Desplazamientos laterales rápidos en posición de defensa. Mejora el footwork y la agilidad lateral. Fundamental para el movimiento de pie en BJJ.',
    'CARDIO', 'HOME', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Agility Ladder Drill',
    'Ejercicios de pies en escalera de agilidad en el suelo. Múltiples patrones. Mejora la coordinación, el ritmo y la agilidad del footwork de lucha de pie.',
    'CARDIO', 'HOME', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

-- =====================================================================
-- HOME — CORE y MOBILITY (10)
-- =====================================================================

(1, 'Ab Wheel Rollout',
    'Con rueda abdominal desde rodillas: extender el cuerpo hacia adelante manteniendo la neutralidad lumbar y volver. El ejercicio de core más exigente.',
    'CORE', 'HOME', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Resistance Band Woodchop',
    'Rotación diagonal del tronco con banda: desde cadera baja a hombro alto. Trabaja la cadena de rotación. Simula el movimiento de barridos y volteos.',
    'CORE', 'HOME', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Pallof Press',
    'Press isométrico anti-rotación con banda: resistir la rotación mientras se extienden los brazos. Entrena el core para estabilizar bajo carga rotacional.',
    'CORE', 'HOME', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Dragon Flag',
    'Desde un banco o escalón: todo el cuerpo sube y baja rígido desde los hombros. Uno de los ejercicios de core más difíciles. Recto abdominal total.',
    'CORE', 'HOME', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Yoga Hip Opener',
    'Secuencia de aperturas de cadera: pigeon pose, figura 4, lagartija profunda. 10-15 min diarios transforman la flexibilidad de cadera para la guardia.',
    'FLEXIBILITY', 'HOME', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Thoracic Spine Rotation',
    'Desde posición de cuadrupedia o de lado, rotar la columna torácica abriendo el pecho. Mejora el giro del tronco para barridos y control de espalda.',
    'MOBILITY', 'HOME', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Couch Stretch',
    'Estiramiento de psoas con el pie trasero contra la pared o silla. Alivia la tensión de cadera de quienes pasan muchas horas sentados. Esencial pre-entrenamiento.',
    'FLEXIBILITY', 'HOME', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Foam Roller Thoracic',
    'Extensión de columna torácica sobre rodillo o toalla enrollada. Contrarresta la flexión constante del BJJ. Mejora la postura y el rango de extensión.',
    'MOBILITY', 'HOME', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Wall Hip Flexor Stretch',
    'Estiramiento de hip flexor contra la pared: rodilla trasera en el suelo, pelvis en anteversión, empujar la cadera hacia adelante y abajo.',
    'FLEXIBILITY', 'HOME', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Banded Shoulder Distraction',
    'Banda fijada en alto, meter el brazo y dejar que la tracción separe la cabeza humeral del acromion. Recuperación y movilidad de hombro post-entrenamiento.',
    'MOBILITY', 'HOME', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

-- =====================================================================
-- GYM — STRENGTH (15)
-- =====================================================================

(1, 'Deadlift',
    'Peso muerto convencional. El ejercicio de fuerza total más transferible al BJJ. Trabaja isquiotibiales, glúteo, espalda baja y grip. Base de todo programa de fuerza.',
    'STRENGTH', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Romanian Deadlift',
    'Peso muerto rumano: sin doblar las rodillas, bajar la barra por las piernas hasta sentir tensión en isquios. Trabaja la cadena posterior que potencia los derribos.',
    'STRENGTH', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Barbell Back Squat',
    'Sentadilla con barra en trapecio. El rey de los ejercicios de tren inferior. Trabaja cuádriceps, glúteo e isquios. Potencia directa en barridos y derribos.',
    'STRENGTH', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Front Squat',
    'Sentadilla frontal con barra en hombros. Mayor demanda en cuádriceps y core erector. Postura más vertical, transferible al shot de lucha libre.',
    'STRENGTH', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Bench Press',
    'Press de banca con barra o mancuernas. Trabaja pectoral, deltoides anterior y tríceps. Fuerza de empuje horizontal relevante para el framing.',
    'STRENGTH', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Barbell Row',
    'Remo con barra. Cuerpo paralelo al suelo, tirar la barra hacia el abdomen. El mejor ejercicio de tracción horizontal para el grip y la espalda del BJJ.',
    'STRENGTH', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Weighted Pull-Up',
    'Dominada lastrada con cinturón o chaleco. Progresión de la dominada estándar. Desarrolla la fuerza de latísimo necesaria para el clinch y el arm drag.',
    'STRENGTH', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Overhead Press',
    'Press militar con barra o mancuernas de pie. Trabaja el deltoides medio y anterior, tríceps y core. Estabilidad de hombro para submissions y control.',
    'STRENGTH', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Hex Bar Deadlift',
    'Peso muerto con barra hexagonal. Posición más vertical, mayor activación de cuádriceps. Menor carga en la espalda baja. Ideal para atletas de combate.',
    'STRENGTH', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Dumbbell Farmer Walk',
    'Cargar mancuernas pesadas y caminar. El ejercicio de grip y core más funcional para BJJ. Trabaja la musculatura de soporte de forma isométrica bajo carga.',
    'STRENGTH', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Power Clean',
    'Arrancada de potencia desde el suelo a los hombros. Desarrolla la potencia explosiva total. Muy relevante para la velocidad de entrada en derribos.',
    'STRENGTH', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Barbell Hip Thrust',
    'Hip thrust con barra sobre cadera. Máxima activación del glúteo mayor. Potencia de cadera para barridos explosivos y escapes desde bottom mount.',
    'STRENGTH', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Face Pull',
    'Remo en polea alta jalando hacia la cara. Trabaja deltoides posterior, trapecios medios y manguito rotador. Previene lesiones de hombro por desequilibrio.',
    'STRENGTH', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Lat Pulldown',
    'Jalón en polea alta. Simula el pull-up con posibilidad de ajustar el peso. Trabaja latísimo del dorso, bíceps y estabilizadores escapulares.',
    'STRENGTH', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Sled Push',
    'Empujar trineo cargado. Acondicionamiento de fuerza resistencia del tren inferior. Muy específico para el drive del double leg y el body lock pass.',
    'STRENGTH', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

-- =====================================================================
-- GYM — CARDIO (5)
-- =====================================================================

(1, 'Rowing Machine',
    'Remo en ergómetro. Trabaja todo el cuerpo en un patrón de tracción. Alta demanda cardiovascular. Intervalos de 500 m para simular la intensidad de un round.',
    'CARDIO', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Assault Bike',
    'Bicicleta de aire con manubrios de brazos. El acondicionador más brutal para BJJ. Intervalos Tabata en assault bike elevan la capacidad anaeróbica máxima.',
    'CARDIO', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Battle Rope',
    'Ondas con cuerdas pesadas. Simula el clinch pummeling y el barrido de brazos. Intervals de 20-30 s con descanso. Cardio de tren superior altamente específico.',
    'CARDIO', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Ski Erg',
    'Máquina de remo de pie simulando esquí de fondo. Patrón de jalón bilateral muy específico para el arrastre de brazos en clinch. Alta demanda cardiovascular.',
    'CARDIO', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Treadmill Sprint',
    'Sprints en cinta a velocidad máxima. Intervalos cortos (10-15 s) con recuperación. Desarrolla el sistema de fosfágenos para las explosiones de BJJ.',
    'CARDIO', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

-- =====================================================================
-- GYM — CORE (5)
-- =====================================================================

(1, 'Cable Woodchop',
    'Rotación diagonal con polea: desde cadera hacia hombro contrario. Trabaja oblicuos y la cadena de rotación. Específico para barridos y control desde guardia.',
    'CORE', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Cable Anti-Rotation Press',
    'Pallof press con polea: resistir la rotación mientras se extienden los brazos hacia afuera. Entrena el core para resistir las fuerzas rotacionales de BJJ.',
    'CORE', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Landmine Rotation',
    'Barra en landmine: sujetar la barra con brazos extendidos y rotar de cadera a cadera. Potencia rotatoria con carga. Muy específico para sweeps y throws.',
    'CORE', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Hanging Leg Raise',
    'Colgado de la barra, elevar las piernas juntas a 90° o hasta el pecho. El mejor ejercicio de core inferior y agarre simultáneo. Durísimo para el grip.',
    'CORE', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Weighted Plank',
    'Plancha estándar con peso encima de la espalda. Progresión de la plancha para mayor demanda isométrica. Trabaja toda la cadena anterior del tronco.',
    'CORE', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

-- =====================================================================
-- GYM — MOBILITY y FLEXIBILITY (5)
-- =====================================================================

(1, 'Band-Assisted Hip Flexor',
    'Con banda fijada en rack: tracción de cadera hacia atrás en lunge position. Libera la tensión del psoas iliaco después de sesiones de BJJ intensas.',
    'MOBILITY', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Box Hip Flexion',
    'Apoyado en un plyo box, llevar la rodilla al pecho con control. Trabaja el rango activo de flexión de cadera, clave para guardar las piernas en guardia.',
    'MOBILITY', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Nordic Hamstring Curl',
    'Arrodillado con pies fijos, bajar el tronco controlando con los isquios. El mejor ejercicio excéntrico para prevenir roturas de isquiotibiales.',
    'STRENGTH', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Cable External Rotation',
    'Rotación externa de hombro en polea: codo a 90°, rotar hacia afuera. Fortalece el manguito rotador. Prevención de la lesión de hombro más común en BJJ.',
    'MOBILITY', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Jefferson Curl',
    'Peso muerto con flexión vertebral progresiva e intencional. Fortalece la cadena posterior en longitud. Solo para practicantes avanzados con buena técnica base.',
    'FLEXIBILITY', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

-- =====================================================================
-- GYM — OTHER / FUNCTIONAL BJJ (5)
-- =====================================================================

(1, 'Kettlebell Swing',
    'Balanceo de kettlebell con explosión de cadera. Trabaja la cadena posterior completa de forma balística. Directamente transferible al hip bump sweep y escapes.',
    'OTHER', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Turkish Get-Up',
    'Levantarse del suelo a de pie con kettlebell en alto. Integra estabilidad de hombro, movilidad de cadera y fuerza funcional total. Movimiento muy específico para BJJ.',
    'OTHER', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Grip Training (gi)',
    'Ejercicios específicos de agarre: towel pull-ups, gi pull-ups, plate pinch, bucket de arroz. Desarrolla el grip indispensable para el juego de gi.',
    'OTHER', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Medicine Ball Slam',
    'Lanzar un balón medicinal con fuerza al suelo desde arriba de la cabeza. Desarrolla la potencia de tracción hacia abajo. Específico para guillotinas y mat returns.',
    'OTHER', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),

(1, 'Rotational Medicine Ball Throw',
    'Lanzar el balón medicinal contra la pared con rotación de cadera. Potencia rotatoria explícita. Transferible a todos los sweeps y proyecciones del BJJ.',
    'OTHER', 'GYM', NULL, 'PUBLIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);
