-- V231__fix_youtube_urls_verified.sql
-- YouTube URLs verificadas manualmente con vídeos funcionales reales

-- ============================================================
-- SUBMISSIONS
-- ============================================================

-- Bernardo Faria / Henry Akins: Hidden detail of the Rear Naked Choke (16 min tutorial)
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=7TChXOpEx5M' WHERE owner_id = 1 AND name = 'Rear Naked Choke';

-- Matt Arroyo JJ: How To Do The Guillotine Choke | The Jiu Jitsu Class (6 min)
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=KrZn5eJ-j4Q' WHERE owner_id = 1 AND name = 'Guillotine Choke';

-- BJJ Fanatics: Cómo hacer el Triángulo en Jiu Jitsu - Todo lo que necesitas saber (11 min)
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=20j7LcZ5xRY' WHERE owner_id = 1 AND name = 'Triangle Choke';

-- Stephan Kesting: How to do the Cutting Armbar (6 min)
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=hDmXQz0JT-8' WHERE owner_id = 1 AND name = 'Armbar';

-- Bernardo Faria BJJ Fanatics: Cómo hacer la kimura perfecta desde el control lateral por John Danaher (10 min)
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=p-6lmaseoGI' WHERE owner_id = 1 AND name = 'Kimura';

-- TutoFighting: Omoplata desde la guardia (3 min)
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=AyqD_5qtZTY' WHERE owner_id = 1 AND name = 'Omoplata';

-- Lachlan Giles: Inside Heel Hook Setup and Safety (6 min)
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=w-W0ug7Edag' WHERE owner_id = 1 AND name = 'Inside Heel Hook';

-- Jason Rau: How to Perfect the Outside Heel Hook Finish (7 min)
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=zQ-FNH5a01k' WHERE owner_id = 1 AND name = 'Outside Heel Hook';

-- Stephan Kesting: Straight Ankle Lock for White Belts - Powerful Details (11 min)
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=5ZAqUQpsus8' WHERE owner_id = 1 AND name = 'Straight Ankle Lock';

-- BJJ: La primera llave de rodilla que debes aprender (6 min)
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=oBlMI4iKm3c' WHERE owner_id = 1 AND name = 'Kneebar';

-- Chewjitsu / BJJ Fanatics: The Toe Hold - How & When to Use It (9 min)
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=7wVbUS0jCts' WHERE owner_id = 1 AND name = 'Toe Hold';

-- ============================================================
-- SWEEPS
-- ============================================================

-- Stephan Kesting: Scissor Sweep for White Belts (5 min)
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=UBf7uF5x8GQ' WHERE owner_id = 1 AND name = 'Scissor Sweep';

-- Jon Thomas / Stephan Kesting: The BJJ Hip Bump Sweep (7 min)
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=86eSdQYSjxA' WHERE owner_id = 1 AND name = 'Hip Bump Sweep';

-- Stephan Kesting: Butterfly Guard Sweep Tips and 2 BJJ Solo Drills (5 min)
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=x7Wv0w9EEcc' WHERE owner_id = 1 AND name = 'Butterfly Sweep';

-- Stephan Kesting: A Simple and Surprisingly Effective De La Riva Sweep (10 min)
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=J8QJ3tOEBsY' WHERE owner_id = 1 AND name = 'De La Riva Sweep';

-- BJJ: The 4 Directions of Every X Guard Sweep (3 min)
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=oe-xUmRLcUg' WHERE owner_id = 1 AND name = 'X-Guard Sweep';

-- ============================================================
-- PASSES
-- ============================================================

-- Toreando Pass tutorial (6 min)
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=bqilRrhC96c' WHERE owner_id = 1 AND name = 'Torreando Pass';

-- Stephan Kesting: Cómo configurar correctamente el pase de rodilla con corte (7 min)
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=jiBiDvrz9iw' WHERE owner_id = 1 AND name = 'Knee Slice Pass';

-- Stephan Kesting: How to Do the Leg Drag Pass, Theory and Practice (3 min)
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=n596l_pJi-E' WHERE owner_id = 1 AND name = 'Leg Drag Pass';

-- Bernardo Faria: How to Pass With Over Under (5 min)
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=4hAY12ghrGk' WHERE owner_id = 1 AND name = 'Over-Under Pass';

-- Jeff Glover: Passing With Double Unders (4 min)
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=PizdshB63kw' WHERE owner_id = 1 AND name = 'Double Under Pass';

-- ============================================================
-- TAKEDOWNS
-- ============================================================

-- Gracie Barra: Derribo básico de doble pierna (6 min)
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=4DHzLvLd-0Y' WHERE owner_id = 1 AND name = 'Double Leg Takedown';

-- Andre Galvao: The Best Single Leg Takedown For Brazilian Jiu Jitsu (10 min)
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=4HBVdF5AXc0' WHERE owner_id = 1 AND name = 'Single Leg Takedown';

-- OGOSHI Hip-Throw Tutorial - Judo Seminar at High Noon BJJ (3 min)
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=lV59S5JADbQ' WHERE owner_id = 1 AND name = 'Hip Throw';

-- Judo for BJJ: Ippon Seoi Nage Without Having Your Back Taken (5 min)
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=ey3VCTdxBYU' WHERE owner_id = 1 AND name = 'Shoulder Throw';

-- ============================================================
-- ESCAPES
-- ============================================================

-- Grappling Fundamentals: Bridge & Roll Escape from Mount (2 min)
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=UyxNPnR24hE' WHERE owner_id = 1 AND name = 'Bridge and Roll';

-- Stephan Kesting: The First 3 Mount Escapes (elbow-knee at timestamp 2:05) (4 min)
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=SYel-mVSMAI' WHERE owner_id = 1 AND name = 'Elbow-Knee Escape';

-- Chewjitsu: How to Escape the Back EVERY TIME | Jiu Jitsu Back Escape System (11 min)
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=N6lKNqUOZoY' WHERE owner_id = 1 AND name = 'Back Escape';

-- Chad Lyman: The Ghost Escape (3 min)
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=t13VFvRh5lo' WHERE owner_id = 1 AND name = 'Ghost Escape';

-- ============================================================
-- MOBILITY EXERCISES (BJJ drills)
-- ============================================================

-- Stephan Kesting: Improve Your Shrimp/Hip Escape - BJJ Fundamentals (5 min)
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=D0rTw8IfJDE' WHERE owner_id = 1 AND name = 'Shrimp (Camarón)';

-- Stephan Kesting: The Four Types of Technical Standup in BJJ (7 min)
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=quMmk9Xs2HE' WHERE owner_id = 1 AND name = 'Technical Stand-Up';

-- BJJ solo drill on your back - hip escape (47 s)
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=D6toRF1vM7s' WHERE owner_id = 1 AND name = 'Hip Escape Drill';

-- Solo BJJ Drills - Bridge, Upa, Through (6 min)
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=Qq_yUhdYCUY' WHERE owner_id = 1 AND name = 'Bridge and Roll Drill';

-- Granby Roll Drill | Jiu Jitsu Movement Library (2 min)
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=oiwLe-l4cvM' WHERE owner_id = 1 AND name = 'Granby Roll';

-- ============================================================
-- FLEXIBILITY EXERCISES
-- ============================================================

-- Butterfly Stretch for Hip Flexibility (6 min)
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=VvMg0Bimxe8' WHERE owner_id = 1 AND name = 'Butterfly Stretch';

-- BJJ Hip Mobility / Hip Flexor Stretch: Perfect hip mobility in 7 minutes (8 min)
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=NWVQxUFH9C4' WHERE owner_id = 1 AND name = 'Hip Flexor Stretch';

-- Yoga for BJJ: Pigeon Pose Tutorial | Hip Stretch for Athletes (4 min)
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=0zyhKn7b29I' WHERE owner_id = 1 AND name = 'Pigeon Pose';

-- PNF Frog Stretch (2 min)
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=JDscdeuAzCU' WHERE owner_id = 1 AND name = 'Frog Stretch';

-- PANCAKE STRETCH Flexibility Routine - Follow Along (21 min)
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=zLCHKQVl10g' WHERE owner_id = 1 AND name = 'Pancake Stretch';
