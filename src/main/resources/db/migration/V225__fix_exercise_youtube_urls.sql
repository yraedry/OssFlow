-- V225__fix_exercise_youtube_urls.sql
-- Reemplaza URLs de YouTube incorrectas o eliminadas en ejercicios del seed.
-- Verificadas con YouTube oEmbed API (HTTP 200) el 2026-05-08.
-- Fuentes: Jeff Nippard, AthleanX, NASM, FitnessFAQs, Scott Herman, Calisthenicmovement.

-- Videos eliminados (404)
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=lIZ_C4VJnmc' WHERE name = 'Pike Push-Up'        AND owner_id = 1;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=tZSYZdtbONc' WHERE name = 'Jump Squat'          AND owner_id = 1;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=euxIgQSde8E' WHERE name = 'Plank'               AND owner_id = 1;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=uZqTUwq96iU' WHERE name = 'Hollow Body Hold'    AND owner_id = 1;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=eywCpp0p7lg' WHERE name = 'L-Sit'               AND owner_id = 1;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=rmdn5X_KLkY' WHERE name = 'Pull-Up'             AND owner_id = 1;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=_oyxCn2iSjU' WHERE name = 'Romanian Deadlift'   AND owner_id = 1;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=bEv6CCg2BC8' WHERE name = 'Barbell Squat'       AND owner_id = 1;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=T3N-TO4reLQ' WHERE name = 'Barbell Row'         AND owner_id = 1;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=_RlRDWO2jfg' WHERE name = 'Overhead Press'      AND owner_id = 1;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=xDmFkJxPzeM' WHERE name = 'Hip Thrust'          AND owner_id = 1;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=8OtwXwrJizk' WHERE name = 'Farmer Walk'         AND owner_id = 1;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=pQb2xIGioyQ' WHERE name = 'Battle Ropes'        AND owner_id = 1;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=Sg3Id7Lu8XU' WHERE name = 'Assault Bike'        AND owner_id = 1;

-- Videos con ID incorrecto (apuntaban a otro ejercicio)
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=iP2fjvG0g3w' WHERE name = 'V-Up'                AND owner_id = 1;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=ZhiCSdOVJp0' WHERE name = 'Mountain Climber'    AND owner_id = 1;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=qLBImHhCXSw' WHERE name = 'Burpee'              AND owner_id = 1;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=el1Aa5jrlhg' WHERE name = 'Chin-Up'             AND owner_id = 1;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=BjiyWC_cJW0' WHERE name = 'Dips'                AND owner_id = 1;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=G6a5267YpHM' WHERE name = 'Hanging Knee Raise'  AND owner_id = 1;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=h-A7HiTNZ5c' WHERE name = 'Kettlebell Swing'    AND owner_id = 1;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=aNDUbH_Uv4g' WHERE name = 'Goblet Squat'        AND owner_id = 1;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=G-bxQY57mKc' WHERE name = 'Box Jump'            AND owner_id = 1;
