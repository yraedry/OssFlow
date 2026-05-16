package com.ossflow.catalog.technique.infrastructure.web.dto;

import com.ossflow.catalog.technique.domain.TechniqueFamily;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public record TechniqueFamilyResponse(String value, String label) {

    private static final Map<TechniqueFamily, String> LABELS = Map.ofEntries(
            Map.entry(TechniqueFamily.CLOSED_GUARD,     "Guardia Cerrada"),
            Map.entry(TechniqueFamily.HALF_GUARD,       "Media Guardia"),
            Map.entry(TechniqueFamily.OPEN_GUARD,       "Guardia Abierta"),
            Map.entry(TechniqueFamily.DLR_GUARD,        "De La Riva"),
            Map.entry(TechniqueFamily.BUTTERFLY_GUARD,  "Guardia Mariposa"),
            Map.entry(TechniqueFamily.LEG_ENTANGLEMENT, "Entrelazados"),
            Map.entry(TechniqueFamily.GUARD_PASSES,     "Pasajes de Guardia"),
            Map.entry(TechniqueFamily.CHOKES,           "Estrangulaciones"),
            Map.entry(TechniqueFamily.GUILLOTINES,      "Guillotinas"),
            Map.entry(TechniqueFamily.TRIANGLES,        "Triángulos"),
            Map.entry(TechniqueFamily.ARMBARS,          "Armbars"),
            Map.entry(TechniqueFamily.SHOULDER_LOCKS,   "Kimuras / Americanas"),
            Map.entry(TechniqueFamily.LEG_LOCKS,        "Leg Locks"),
            Map.entry(TechniqueFamily.TAKEDOWNS,        "Derribos"),
            Map.entry(TechniqueFamily.SWEEPS,           "Barridas"),
            Map.entry(TechniqueFamily.BACK_TAKES,       "Tomas de Espalda"),
            Map.entry(TechniqueFamily.ESCAPES,          "Escapadas"),
            Map.entry(TechniqueFamily.OTHER,            "Otro")
    );

    public static List<TechniqueFamilyResponse> all() {
        return Arrays.stream(TechniqueFamily.values())
                .map(f -> new TechniqueFamilyResponse(f.name(), LABELS.getOrDefault(f, f.name())))
                .toList();
    }
}
