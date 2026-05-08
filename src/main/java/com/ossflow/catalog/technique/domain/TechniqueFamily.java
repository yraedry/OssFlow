package com.ossflow.catalog.technique.domain;

public enum TechniqueFamily {
    // Guardias
    CLOSED_GUARD,
    HALF_GUARD,
    OPEN_GUARD,        // Spider, Lasso, Collar-Sleeve, Worm Guard
    DLR_GUARD,         // De La Riva, Reverse DLR
    BUTTERFLY_GUARD,
    LEG_ENTANGLEMENT,  // Single X, X-Guard, 50/50, Saddle, Ashi Garami

    // Pasajes de guardia
    GUARD_PASSES,      // Toreando, Leg Trap, Trípode, Knee Slice, Over-Under

    // Sistemas de sumisión
    CHOKES,            // RNC, Bow & Arrow, Cross Collar, North-South Choke...
    GUILLOTINES,
    TRIANGLES,
    ARMBARS,
    SHOULDER_LOCKS,    // Kimura, Americana, Omoplata
    LEG_LOCKS,         // Heel Hooks, Ankle Lock, Kneebar, Toe Hold

    // Movimiento
    TAKEDOWNS,         // Bombero, Single Leg, Double Leg, Uchi Mata, Seoi Nage
    SWEEPS,
    BACK_TAKES,
    ESCAPES,
    OTHER
}
