-- V209__seed_systems_flow_definition.sql
-- Flow definitions para los 10 sistemas. Nodos = posiciones clave, edges = técnicas que conectan.
-- IDs de posiciones (V200, owner_id=1, orden de inserción):
--   1=Standing Neutral, 2=Clinch, 3=Arm Drag Position, 4=Turtle Top, 5=Full Mount,
--   6=Side Control, 7=North South, 8=Knee On Belly, 9=Back Mount, 10=Turtle,
--   11=Closed Guard, 12=Butterfly Guard, 13=Half Guard, 14=De La Riva Guard,
--   15=Spider Guard, 16=Lasso Guard, 17=X-Guard, 18=Single Leg X, 19=50/50,
--   20=Deep Half Guard, 21=K-Guard, 22=Rubber Guard, 23=Seated Guard,
--   24=Leg Entanglement, 25=Bottom Mount, 26=Bottom Side Control, 27=Bottom Back Mount,
--   28=Bottom Knee On Belly, 29=Submission, 30=Standing Neutral (Bottom)
-- IDs de técnicas relevantes por sistema (aprox):
--   Heel Hook Inside=23, Heel Hook Outside=24, Straight Ankle=25, Kneebar=26,
--   Toe Hold=27, DLR Sweep=36, DLR to SLX=47 (transition), Backstep Pass=51,
--   Leg Drag Pass=43, Berimbolo=48, Spider Sweep=37, Butterfly Sweep=35,
--   X-Guard Sweep=38, SLX Sweep=39, Arm Drag=10, Back Take Guard=49 (transition)

UPDATE system SET flow_definition = '{
  "nodes": [
    {"id": "dlr",    "type": "position", "data": {"label": "De La Riva Guard", "positionId": 14}, "position": {"x": 300, "y": 200}},
    {"id": "slx",    "type": "position", "data": {"label": "Single Leg X",      "positionId": 18}, "position": {"x": 550, "y": 200}},
    {"id": "5050",   "type": "position", "data": {"label": "50/50",              "positionId": 19}, "position": {"x": 700, "y": 350}},
    {"id": "back",   "type": "position", "data": {"label": "Back Mount",         "positionId": 9},  "position": {"x": 550, "y": 50}},
    {"id": "side",   "type": "position", "data": {"label": "Side Control",       "positionId": 6},  "position": {"x": 100, "y": 350}},
    {"id": "sub",    "type": "submission","data": {"label": "Submission",         "positionId": 29}, "position": {"x": 700, "y": 50}}
  ],
  "edges": [
    {"id": "e1", "source": "dlr",  "target": "slx",  "label": "DLR a SLX",     "data": {"techniqueId": 57}},
    {"id": "e2", "source": "dlr",  "target": "back", "label": "Berimbolo",      "data": {"techniqueId": 48}},
    {"id": "e3", "source": "dlr",  "target": "side", "label": "DLR Sweep",      "data": {"techniqueId": 36}},
    {"id": "e4", "source": "slx",  "target": "5050", "label": "Ashi a 50/50",   "data": {"techniqueId": 61}},
    {"id": "e5", "source": "slx",  "target": "sub",  "label": "Heel Hook",      "data": {"techniqueId": 23}},
    {"id": "e6", "source": "5050", "target": "sub",  "label": "Heel Hook",      "data": {"techniqueId": 24}}
  ]
}' WHERE name = 'Sistema De La Riva' AND owner_id = 1;

UPDATE system SET flow_definition = '{
  "nodes": [
    {"id": "spider", "type": "position", "data": {"label": "Spider Guard",   "positionId": 15}, "position": {"x": 300, "y": 200}},
    {"id": "tri",    "type": "submission","data": {"label": "Triangle",       "positionId": 29}, "position": {"x": 500, "y": 50}},
    {"id": "omo",    "type": "submission","data": {"label": "Omoplata",       "positionId": 29}, "position": {"x": 500, "y": 350}},
    {"id": "side",   "type": "position", "data": {"label": "Side Control",   "positionId": 6},  "position": {"x": 100, "y": 200}},
    {"id": "lasso",  "type": "position", "data": {"label": "Lasso Guard",    "positionId": 16}, "position": {"x": 300, "y": 400}}
  ],
  "edges": [
    {"id": "e1", "source": "spider", "target": "tri",   "label": "Triangle desde Spider", "data": {"techniqueId": 3}},
    {"id": "e2", "source": "spider", "target": "omo",   "label": "Omoplata",               "data": {"techniqueId": 18}},
    {"id": "e3", "source": "spider", "target": "side",  "label": "Spider Sweep",           "data": {"techniqueId": 37}},
    {"id": "e4", "source": "spider", "target": "lasso", "label": "Transición a Lasso",     "data": {}}
  ]
}' WHERE name = 'Spider Guard System' AND owner_id = 1;

UPDATE system SET flow_definition = '{
  "nodes": [
    {"id": "lasso",  "type": "position", "data": {"label": "Lasso Guard",    "positionId": 16}, "position": {"x": 300, "y": 200}},
    {"id": "spider", "type": "position", "data": {"label": "Spider Guard",   "positionId": 15}, "position": {"x": 300, "y": 50}},
    {"id": "side",   "type": "position", "data": {"label": "Side Control",   "positionId": 6},  "position": {"x": 100, "y": 200}},
    {"id": "omo",    "type": "submission","data": {"label": "Omoplata",       "positionId": 29}, "position": {"x": 500, "y": 350}},
    {"id": "tri",    "type": "submission","data": {"label": "Triangle",       "positionId": 29}, "position": {"x": 500, "y": 100}}
  ],
  "edges": [
    {"id": "e1", "source": "lasso", "target": "spider", "label": "Collar-Sleeve",      "data": {}},
    {"id": "e2", "source": "lasso", "target": "side",   "label": "Lasso Sweep",        "data": {"techniqueId": 40}},
    {"id": "e3", "source": "lasso", "target": "omo",    "label": "Omoplata",           "data": {"techniqueId": 18}},
    {"id": "e4", "source": "lasso", "target": "tri",    "label": "Triangle",           "data": {"techniqueId": 3}}
  ]
}' WHERE name = 'Lasso Guard System' AND owner_id = 1;

UPDATE system SET flow_definition = '{
  "nodes": [
    {"id": "seated", "type": "position", "data": {"label": "Seated Guard",   "positionId": 23}, "position": {"x": 50,  "y": 200}},
    {"id": "slx",    "type": "position", "data": {"label": "Single Leg X",   "positionId": 18}, "position": {"x": 250, "y": 200}},
    {"id": "5050",   "type": "position", "data": {"label": "50/50",           "positionId": 19}, "position": {"x": 450, "y": 300}},
    {"id": "kguard", "type": "position", "data": {"label": "K-Guard",         "positionId": 21}, "position": {"x": 450, "y": 100}},
    {"id": "ihhook", "type": "submission","data": {"label": "Inside Heel Hook","positionId": 29}, "position": {"x": 650, "y": 200}},
    {"id": "ohhook", "type": "submission","data": {"label": "Outer Heel Hook", "positionId": 29}, "position": {"x": 650, "y": 350}},
    {"id": "ankle",  "type": "submission","data": {"label": "Straight Ankle",  "positionId": 29}, "position": {"x": 650, "y": 50}}
  ],
  "edges": [
    {"id": "e1", "source": "seated", "target": "slx",    "label": "Entrada SLX",      "data": {}},
    {"id": "e2", "source": "slx",    "target": "kguard", "label": "K-Guard",          "data": {}},
    {"id": "e3", "source": "slx",    "target": "5050",   "label": "Ashi a 50/50",     "data": {"techniqueId": 61}},
    {"id": "e4", "source": "slx",    "target": "ankle",  "label": "Straight Ankle",   "data": {"techniqueId": 25}},
    {"id": "e5", "source": "kguard", "target": "ihhook", "label": "Inside Heel Hook", "data": {"techniqueId": 23}},
    {"id": "e6", "source": "5050",   "target": "ohhook", "label": "Outside Heel Hook","data": {"techniqueId": 24}},
    {"id": "e7", "source": "slx",    "target": "ihhook", "label": "Inside Heel Hook", "data": {"techniqueId": 23}}
  ]
}' WHERE name = 'Leg Lock System' AND owner_id = 1;

UPDATE system SET flow_definition = '{
  "nodes": [
    {"id": "kguard", "type": "position", "data": {"label": "K-Guard",          "positionId": 21}, "position": {"x": 300, "y": 200}},
    {"id": "slx",    "type": "position", "data": {"label": "Single Leg X",     "positionId": 18}, "position": {"x": 500, "y": 100}},
    {"id": "dlr",    "type": "position", "data": {"label": "De La Riva Guard", "positionId": 14}, "position": {"x": 100, "y": 200}},
    {"id": "ihhook", "type": "submission","data": {"label": "Inside Heel Hook", "positionId": 29}, "position": {"x": 550, "y": 300}},
    {"id": "side",   "type": "position", "data": {"label": "Side Control",     "positionId": 6},  "position": {"x": 100, "y": 50}}
  ],
  "edges": [
    {"id": "e1", "source": "dlr",    "target": "kguard", "label": "DLR a K-Guard",   "data": {}},
    {"id": "e2", "source": "kguard", "target": "slx",    "label": "K-Guard a SLX",   "data": {}},
    {"id": "e3", "source": "kguard", "target": "ihhook", "label": "Heel Hook",        "data": {"techniqueId": 23}},
    {"id": "e4", "source": "kguard", "target": "side",   "label": "Sweep",            "data": {}}
  ]
}' WHERE name = 'K-Guard System' AND owner_id = 1;

UPDATE system SET flow_definition = '{
  "nodes": [
    {"id": "butterfly","type": "position", "data": {"label": "Butterfly Guard","positionId": 12}, "position": {"x": 300, "y": 200}},
    {"id": "back",     "type": "position", "data": {"label": "Back Mount",     "positionId": 9},  "position": {"x": 500, "y": 100}},
    {"id": "half",     "type": "position", "data": {"label": "Half Guard",     "positionId": 13}, "position": {"x": 100, "y": 300}},
    {"id": "mount",    "type": "position", "data": {"label": "Full Mount",     "positionId": 5},  "position": {"x": 100, "y": 100}},
    {"id": "kimura",   "type": "submission","data": {"label": "Kimura",         "positionId": 29}, "position": {"x": 500, "y": 350}},
    {"id": "rnc",      "type": "submission","data": {"label": "RNC",            "positionId": 29}, "position": {"x": 700, "y": 100}}
  ],
  "edges": [
    {"id": "e1", "source": "butterfly", "target": "back",   "label": "Back Take",         "data": {"techniqueId": 49}},
    {"id": "e2", "source": "butterfly", "target": "mount",  "label": "Butterfly Sweep",   "data": {"techniqueId": 35}},
    {"id": "e3", "source": "butterfly", "target": "half",   "label": "Half Guard",        "data": {}},
    {"id": "e4", "source": "butterfly", "target": "kimura", "label": "Kimura",            "data": {"techniqueId": 16}},
    {"id": "e5", "source": "back",      "target": "rnc",    "label": "Rear Naked Choke",  "data": {"techniqueId": 1}}
  ]
}' WHERE name = 'Butterfly Guard System' AND owner_id = 1;

UPDATE system SET flow_definition = '{
  "nodes": [
    {"id": "half",  "type": "position", "data": {"label": "Half Guard",      "positionId": 13}, "position": {"x": 300, "y": 200}},
    {"id": "dhalf", "type": "position", "data": {"label": "Deep Half Guard", "positionId": 20}, "position": {"x": 300, "y": 400}},
    {"id": "back",  "type": "position", "data": {"label": "Back Mount",      "positionId": 9},  "position": {"x": 550, "y": 200}},
    {"id": "mount", "type": "position", "data": {"label": "Full Mount",      "positionId": 5},  "position": {"x": 100, "y": 100}},
    {"id": "side",  "type": "position", "data": {"label": "Side Control",    "positionId": 6},  "position": {"x": 100, "y": 300}}
  ],
  "edges": [
    {"id": "e1", "source": "half",  "target": "dhalf", "label": "Half a Deep Half",  "data": {"techniqueId": 58}},
    {"id": "e2", "source": "half",  "target": "back",  "label": "Back Take",         "data": {}},
    {"id": "e3", "source": "half",  "target": "mount", "label": "Old School Sweep",  "data": {"techniqueId": 49}},
    {"id": "e4", "source": "dhalf", "target": "mount", "label": "Waiter Sweep",      "data": {"techniqueId": 41}},
    {"id": "e5", "source": "side",  "target": "half",  "label": "Knee Shield",       "data": {}}
  ]
}' WHERE name = 'Half Guard System' AND owner_id = 1;

UPDATE system SET flow_definition = '{
  "nodes": [
    {"id": "closed",  "type": "position", "data": {"label": "Closed Guard",    "positionId": 11}, "position": {"x": 100, "y": 200}},
    {"id": "rubber",  "type": "position", "data": {"label": "Rubber Guard",    "positionId": 22}, "position": {"x": 300, "y": 200}},
    {"id": "mission", "type": "position", "data": {"label": "Mission Control", "positionId": 22}, "position": {"x": 500, "y": 100}},
    {"id": "gogo",    "type": "submission","data": {"label": "Gogoplata",       "positionId": 29}, "position": {"x": 700, "y": 100}},
    {"id": "twister", "type": "submission","data": {"label": "Twister",         "positionId": 29}, "position": {"x": 700, "y": 300}}
  ],
  "edges": [
    {"id": "e1", "source": "closed",  "target": "rubber",  "label": "Pierna al cuello",   "data": {}},
    {"id": "e2", "source": "rubber",  "target": "mission", "label": "Mission Control",    "data": {}},
    {"id": "e3", "source": "mission", "target": "gogo",    "label": "Gogoplata",          "data": {"techniqueId": 21}},
    {"id": "e4", "source": "rubber",  "target": "twister", "label": "Twister",            "data": {}}
  ]
}' WHERE name = 'Rubber Guard System' AND owner_id = 1;

UPDATE system SET flow_definition = '{
  "nodes": [
    {"id": "back",  "type": "position", "data": {"label": "Back Mount",   "positionId": 9},  "position": {"x": 300, "y": 200}},
    {"id": "mount", "type": "position", "data": {"label": "Full Mount",   "positionId": 5},  "position": {"x": 100, "y": 200}},
    {"id": "side",  "type": "position", "data": {"label": "Side Control", "positionId": 6},  "position": {"x": 100, "y": 350}},
    {"id": "rnc",   "type": "submission","data": {"label": "RNC",          "positionId": 29}, "position": {"x": 550, "y": 100}},
    {"id": "bow",   "type": "submission","data": {"label": "Bow & Arrow",  "positionId": 29}, "position": {"x": 550, "y": 300}},
    {"id": "armbar","type": "submission","data": {"label": "Armbar",       "positionId": 29}, "position": {"x": 550, "y": 50}}
  ],
  "edges": [
    {"id": "e1", "source": "mount", "target": "back",   "label": "Mount a Espalda",    "data": {"techniqueId": 63}},
    {"id": "e2", "source": "side",  "target": "back",   "label": "Side a Espalda",     "data": {"techniqueId": 64}},
    {"id": "e3", "source": "back",  "target": "rnc",    "label": "Rear Naked Choke",   "data": {"techniqueId": 1}},
    {"id": "e4", "source": "back",  "target": "bow",    "label": "Bow and Arrow",      "data": {"techniqueId": 5}},
    {"id": "e5", "source": "back",  "target": "armbar", "label": "Armbar desde espalda","data": {"techniqueId": 15}}
  ]
}' WHERE name = 'Back Attack System' AND owner_id = 1;

UPDATE system SET flow_definition = '{
  "nodes": [
    {"id": "butterfly","type": "position", "data": {"label": "Butterfly Guard","positionId": 12}, "position": {"x": 100, "y": 200}},
    {"id": "xguard",   "type": "position", "data": {"label": "X-Guard",        "positionId": 17}, "position": {"x": 300, "y": 200}},
    {"id": "slx",      "type": "position", "data": {"label": "Single Leg X",   "positionId": 18}, "position": {"x": 500, "y": 300}},
    {"id": "standing", "type": "position", "data": {"label": "Standing",       "positionId": 1},  "position": {"x": 500, "y": 100}},
    {"id": "back",     "type": "position", "data": {"label": "Back Mount",     "positionId": 9},  "position": {"x": 300, "y": 50}}
  ],
  "edges": [
    {"id": "e1", "source": "butterfly", "target": "xguard",   "label": "Butterfly a X-Guard",   "data": {}},
    {"id": "e2", "source": "xguard",    "target": "standing", "label": "X-Guard Sweep",         "data": {"techniqueId": 38}},
    {"id": "e3", "source": "xguard",    "target": "slx",      "label": "X-Guard a SLX",         "data": {}},
    {"id": "e4", "source": "xguard",    "target": "back",     "label": "Arm Drag a Espalda",    "data": {"techniqueId": 10}}
  ]
}' WHERE name = 'X-Guard System' AND owner_id = 1;
