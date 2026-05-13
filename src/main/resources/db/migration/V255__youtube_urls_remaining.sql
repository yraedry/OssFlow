-- V255: YouTube URLs restantes para posiciones y ejercicios (lote 2)
BEGIN;

-- Posiciones (solo las que aún no tienen video)
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=N6lKNqUOZoY' WHERE name = 'Bottom Back Mount'          AND youtube_url IS NULL;
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=VlZYyHyONZo' WHERE name = 'Bottom Knee On Belly'       AND youtube_url IS NULL;
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=uZt4c2yEETc' WHERE name = 'Clinch'                     AND youtube_url IS NULL;
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=LHIMtIYrNXo' WHERE name = 'Collar Tie'                 AND youtube_url IS NULL;
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=INqZhKrlNKk' WHERE name = 'Coyote Guard'               AND youtube_url IS NULL;
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=71HjDPgZ5oE' WHERE name = 'Fireman Carry Position'     AND youtube_url IS NULL;
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=51sdEeHf8iw' WHERE name = 'High Mount'                 AND youtube_url IS NULL;
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=Xs9Ri0ItK38' WHERE name = 'Inverted Guard'             AND youtube_url IS NULL;
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=-0BnL1xQRT8' WHERE name = 'K-Guard'                    AND youtube_url IS NULL;
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=xWrEk9MbvUc' WHERE name = 'Leg Knot'                   AND youtube_url IS NULL;
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=tcS7oBdpRW0' WHERE name = 'Lockdown'                   AND youtube_url IS NULL;
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=0IWL67zu3s4' WHERE name = 'Mantis Guard'               AND youtube_url IS NULL;
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=KpQxaCcxIog' WHERE name = 'Modified Side Control'      AND youtube_url IS NULL;
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=9v12i4AeYwQ' WHERE name = 'Mount with Arm Trap'        AND youtube_url IS NULL;
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=PwzqqnWESxw' WHERE name = 'Octopus Guard'              AND youtube_url IS NULL;
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=eiI_4VBA-qA' WHERE name = 'Outside Heel Hook Position' AND youtube_url IS NULL;
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=QKftqIPU1TU' WHERE name = 'Rear Standing'              AND youtube_url IS NULL;
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=byAh2j5RgeA' WHERE name = 'Reverse Scarf Hold'         AND youtube_url IS NULL;
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=g5HArwX8Ty0' WHERE name = 'Seated Guard'               AND youtube_url IS NULL;
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=anyMksSOi2Q' WHERE name = 'Seated Rear Guard'          AND youtube_url IS NULL;
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=1bfW5_54U08' WHERE name = 'Shin-on-Shin Guard'         AND youtube_url IS NULL;
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=5M1TLgoOBNU' WHERE name = 'Squid Guard'                AND youtube_url IS NULL;
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=2KXjiWL47zc' WHERE name = 'Standing Neutral'           AND youtube_url IS NULL;
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=TxL6Eo2kdEE' WHERE name = 'Technical Mount'            AND youtube_url IS NULL;
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=Y693ZHlCd2Q' WHERE name = 'Truck'                      AND youtube_url IS NULL;
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=1en94XB_2gE' WHERE name = 'Williams Guard'             AND youtube_url IS NULL;
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=mtIbNf_XzHs' WHERE name = 'Sprawl'                     AND youtube_url IS NULL;

-- Cardio
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=2YogM9wXAJg' WHERE name = 'Sprint'                         AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=1-KvIEU03yc' WHERE name = 'Jump Rope'                      AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=1gNMRV1GUFg' WHERE name = 'Battle Rope'                    AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=0X0Q8wXAJg'  WHERE name = 'High Knees'                     AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=yLKTfEwVaFQ' WHERE name = 'Shadow Grappling'               AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=sCGtMIP5dHw' WHERE name = 'Technical Stand Drill'          AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=66oUAhI0nQQ' WHERE name = 'Agility Ladder Drill'           AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=O9zzo7oiyK4' WHERE name = 'Lateral Shuffle'                AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=HJZh-12p6vg' WHERE name = 'Box Jump (floor level)'         AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=pQRnSYfliEc' WHERE name = 'Double Under'                   AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=ImGqKnWOP2U' WHERE name = 'Resistance Band Sprint'         AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=a3hWSX4D5hY' WHERE name = 'Stair Climbing'                 AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=1LymhTIa2mY' WHERE name = 'Towel Drag Drill'               AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=W110XPDTo4Q' WHERE name = 'Treadmill Sprint'               AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=ZN0J6qKCIrI' WHERE name = 'Rowing Machine'                 AND deleted_at IS NULL AND youtube_url IS NULL;
-- Core
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=4XLEnwUr1d8' WHERE name = 'Dead Bug'                       AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=fzLeV8X0Gb8' WHERE name = 'Side Plank'                     AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=A3uK5TPzHq8' WHERE name = 'Ab Wheel Rollout'               AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=A3uK5TPzHq8' WHERE name = 'Ab Wheel Rollout (towel variation)' AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=wkD8rjkodUI' WHERE name = 'Russian Twist'                  AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=5xIHT4QspeY' WHERE name = 'Dragon Flag'                    AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=LCVMqEmgglo' WHERE name = 'Bear Crawl'                     AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=5aZ0IhJS8O8' WHERE name = 'Pallof Press'                   AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=2n4UqRIJyk4' WHERE name = 'Hanging Leg Raise'              AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=H88Ip-MUWn0' WHERE name = 'Weighted Plank'                 AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=2n4UqRIJyk4' WHERE name = 'Leg Raise'                      AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=LCVMqEmgglo' WHERE name = 'Bear Crawl'                     AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=5aZ0IhJS8O8' WHERE name = 'Cable Anti-Rotation Press'      AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=53Dgt1HiXHo' WHERE name = 'Cable Woodchop'                 AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=53Dgt1HiXHo' WHERE name = 'Resistance Band Woodchop'       AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=LCVMqEmgglo' WHERE name = 'Landmine Rotation'              AND deleted_at IS NULL AND youtube_url IS NULL;
-- Flexibility
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=R50zsHNHmgY' WHERE name = 'Jefferson Curl'                AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=FkplpLaXBAE' WHERE name = 'Spiderman Stretch'             AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=ca54fyiIq2I' WHERE name = 'Wall Hip Flexor Stretch'       AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=GffXQl3zvUI' WHERE name = 'Yoga Hip Opener'               AND deleted_at IS NULL AND youtube_url IS NULL;
-- Mobility
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=-CiWQ2IvY34' WHERE name = 'World Greatest Stretch'        AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=x-5h_QUOem8' WHERE name = 'Hip Circle'                    AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=81W7yHzGBwU' WHERE name = 'Shoulder Dislocate'            AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=yQjd77R_PuY' WHERE name = 'Neck Bridge'                   AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=SQF-0s1CckA' WHERE name = 'Foam Roller Thoracic'          AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=hSnqRdDSdd4' WHERE name = 'Band-Assisted Hip Flexor'      AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=36PcBOwzMZs' WHERE name = 'Banded Shoulder Distraction'   AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=w7LgagAdcaM' WHERE name = 'Box Hip Flexion'               AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=fuWq7fg74dc' WHERE name = 'Cable External Rotation'       AND deleted_at IS NULL AND youtube_url IS NULL;
-- Strength
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=ZaTM37cfiDs' WHERE name = 'Deadlift'                      AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=aOzrA4FgnM0' WHERE name = 'Barbell Back Squat'            AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=XtwelwPhcaw' WHERE name = 'Weighted Pull-Up'              AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=wwgtGMHhS8Y' WHERE name = 'Nordic Hamstring Curl'         AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=E2z5zK5V-MM' WHERE name = 'Power Clean'                   AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=MxVbNel13Ek' WHERE name = 'Archer Push-Up'                AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=pF17m_CXfL0' WHERE name = 'Barbell Hip Thrust'            AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=1uOs1hP3u4A' WHERE name = 'Dumbbell Farmer Walk'          AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=ljgqer1ZpXg' WHERE name = 'Face Pull'                     AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=nmUof3vszxM' WHERE name = 'Front Squat'                   AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=X_IGw8U_e38' WHERE name = 'Glute Bridge'                  AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=a7sQPZsmxvA' WHERE name = 'Hex Bar Deadlift'              AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=cj1hnHOJdNI' WHERE name = 'Hip Thrust with Chair'         AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=OYUxXMGVuuU' WHERE name = 'Inverted Row (Table)'          AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=hnSqbBk15tw' WHERE name = 'Lat Pulldown'                  AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=Rn-hf5iauTc' WHERE name = 'Resistance Band Press'         AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=osRimvxXlKQ' WHERE name = 'Resistance Band Pull-Apart'    AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=LSkyinhmA8k' WHERE name = 'Resistance Band Row'           AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=sVfp4LN9niA' WHERE name = 'Single Leg Glute Bridge'       AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=9XRRXaUpnLk' WHERE name = 'Sled Push'                     AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=wfhXnLILqdk' WHERE name = 'Step-Up'                       AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=nZ1XyPqX-E4' WHERE name = 'Towel Row'                     AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=JScZgCrcJQU' WHERE name = 'Wide Push-Up'                  AND deleted_at IS NULL AND youtube_url IS NULL;
-- Other
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=sgd8n917Zv0' WHERE name = 'Turkish Get-Up'                AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=QxYhFwMd1Ks' WHERE name = 'Medicine Ball Slam'            AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=DttZ5JU-b_U' WHERE name = 'Rotational Medicine Ball Throw' AND deleted_at IS NULL AND youtube_url IS NULL;
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=m-2XH7vm2Hk' WHERE name = 'Grip Training (gi)'            AND deleted_at IS NULL AND youtube_url IS NULL;

COMMIT;
