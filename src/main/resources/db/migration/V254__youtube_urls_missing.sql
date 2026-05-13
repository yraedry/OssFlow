-- V254: URLs de YouTube para técnicas, posiciones y ejercicios sin video
-- Videos verificados en YouTube (mayo 2026)

-- ============================================================
-- TÉCNICAS SIN VIDEO
-- ============================================================

UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=xqNhZVNhxnE'
WHERE name = 'Bow and Arrow Choke' AND deleted_at IS NULL;

UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=HuwV3-g2knA'
WHERE name = 'North-South Choke' AND deleted_at IS NULL;

UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=awCHI_VFJQI'
WHERE name = 'Elevator Sweep' AND deleted_at IS NULL;

-- Overhead Sweep — BJJ Fanatics / Bia Mesquita
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=LowKXx1vI4E'
WHERE name = 'Overhead Sweep' AND deleted_at IS NULL;

UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=ZvhT-y88kk8'
WHERE name = 'Spider Guard Sweep' AND deleted_at IS NULL;

-- ============================================================
-- POSICIONES SIN VIDEO
-- ============================================================

UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=mKbMqB6I9FI' WHERE name = '50/50';
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=N6lKNqUOZoY' WHERE name = 'Bottom Back Mount';
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=BHUYEm0ve9A' WHERE name = 'Bottom Knee On Belly';
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=CKrKMjZgGU4' WHERE name = 'Clinch';
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=MR1gQnRPZ_o' WHERE name = 'Collar Tie';
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=INqZhKrlNKk' WHERE name = 'Coyote Guard';
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=rCQsaENYkTE' WHERE name = 'Crucifix';
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=eiI_4VBA-qA' WHERE name = 'Double Outside Ashi';
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=71HjDPgZ5oE' WHERE name = 'Fireman Carry Position';
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=_uuWD4CxRDk' WHERE name = 'High Mount';
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=JuYWROW4MNc' WHERE name = 'Inverted Guard';
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=2b7bqY7iZBs' WHERE name = 'K-Guard';
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=xyCakxmx-2E' WHERE name = 'Kimura Trap';
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=xWrEk9MbvUc' WHERE name = 'Leg Knot';
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=tcS7oBdpRW0' WHERE name = 'Lockdown';
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=0IWL67zu3s4' WHERE name = 'Mantis Guard';
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=kWxijyG-6BE' WHERE name = 'Modified Side Control';
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=KPJJENQtRWU' WHERE name = 'Mount with Arm Trap';
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=LQyn3FMkIto' WHERE name = 'Octopus Guard';
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=zQ-FNH5a01k' WHERE name = 'Outside Heel Hook Position';
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=rFIoeIB4-A4' WHERE name = 'Rear Standing';
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=XTRu2aRFwMM' WHERE name = 'Reverse Scarf Hold';
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=g5HArwX8Ty0' WHERE name = 'Seated Guard';
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=d2n-R6yvw-g' WHERE name = 'Seated Rear Guard';
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=1bfW5_54U08' WHERE name = 'Shin-on-Shin Guard';
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=5M1TLgoOBNU' WHERE name = 'Squid Guard';
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=j5uF1s3g-sM' WHERE name = 'Standing Neutral';
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=DZ-fmNcJFg4' WHERE name = 'Technical Mount';
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=IY0CbahJG2A' WHERE name = 'Truck';
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=1en94XB_2gE' WHERE name = 'Williams Guard';
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=Adz0whT8wBc' WHERE name = 'Sprawl';

-- ============================================================
-- EJERCICIOS SIN VIDEO
-- ============================================================

-- CARDIO
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=3Du0OiBJz0Y' WHERE name = 'Sprint' AND deleted_at IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=1BZM2Vre5oc' WHERE name = 'Jump Rope' AND deleted_at IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=pQb2xIGioyQ' WHERE name = 'Battle Rope' AND deleted_at IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=QPfOZ0e30xg' WHERE name = 'High Knees' AND deleted_at IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=yLKTfEwVaFQ' WHERE name = 'Shadow Grappling' AND deleted_at IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=yC_sSqO4Vx0' WHERE name = 'Technical Stand Drill' AND deleted_at IS NULL;

-- CORE
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=4XLEnwUr1d8' WHERE name = 'Dead Bug' AND deleted_at IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=N_s9em1xTqU' WHERE name = 'Side Plank' AND deleted_at IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=A3uK5TPzHq8' WHERE name = 'Ab Wheel Rollout' AND deleted_at IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=wkD8rjkodUI' WHERE name = 'Russian Twist' AND deleted_at IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=kICxJien7xM' WHERE name = 'Dragon Flag' AND deleted_at IS NULL;

-- FLEXIBILITY
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=rJfehSZOZyU' WHERE name = 'Jefferson Curl' AND deleted_at IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=FkplpLaXBAE' WHERE name = 'Spiderman Stretch' AND deleted_at IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=NVzs5gy11wQ' WHERE name = 'Wall Hip Flexor Stretch' AND deleted_at IS NULL;

-- MOBILITY
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=-CiWQ2IvY34' WHERE name = 'World Greatest Stretch' AND deleted_at IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=Wh1Kg2iqBiw' WHERE name = 'Hip Circle' AND deleted_at IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=vP8YmmRMz6I' WHERE name = 'Shoulder Dislocate' AND deleted_at IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=vMMbHjoCgoE' WHERE name = 'Neck Bridge' AND deleted_at IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=SQF-0s1CckA' WHERE name = 'Foam Roller Thoracic' AND deleted_at IS NULL;

-- STRENGTH
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=XxWcirHIwVo' WHERE name = 'Deadlift' AND deleted_at IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=rrJIyZGlK8c' WHERE name = 'Barbell Back Squat' AND deleted_at IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=C0I0gb76yaA' WHERE name = 'Weighted Pull-Up' AND deleted_at IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=_e9vFU9-tkc' WHERE name = 'Nordic Hamstring Curl' AND deleted_at IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=5vVSGITznQk' WHERE name = 'Power Clean' AND deleted_at IS NULL;
