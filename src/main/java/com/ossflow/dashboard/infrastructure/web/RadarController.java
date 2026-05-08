package com.ossflow.dashboard.infrastructure.web;

import com.ossflow.catalog.technique.domain.TechniqueFamily;
import com.ossflow.dashboard.infrastructure.web.dto.RadarDataPoint;
import com.ossflow.shared.web.CurrentOwner;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/analisis")
@RequiredArgsConstructor
public class RadarController {

    private final JdbcClient jdbcClient;
    private final CurrentOwner currentOwner;

    private static final Map<TechniqueFamily, String> FAMILY_LABELS = Map.ofEntries(
        Map.entry(TechniqueFamily.CLOSED_GUARD,     "Guardia Cerrada"),
        Map.entry(TechniqueFamily.HALF_GUARD,       "Media Guardia"),
        Map.entry(TechniqueFamily.OPEN_GUARD,       "Guardia Abierta"),
        Map.entry(TechniqueFamily.DLR_GUARD,        "De La Riva"),
        Map.entry(TechniqueFamily.BUTTERFLY_GUARD,  "Guardia Mariposa"),
        Map.entry(TechniqueFamily.LEG_ENTANGLEMENT, "Leg Locks / Entrelazados"),
        Map.entry(TechniqueFamily.GUARD_PASSES,     "Pasajes"),
        Map.entry(TechniqueFamily.CHOKES,           "Estrangulaciones"),
        Map.entry(TechniqueFamily.GUILLOTINES,      "Guillotinas"),
        Map.entry(TechniqueFamily.TRIANGLES,        "Triángulos"),
        Map.entry(TechniqueFamily.ARMBARS,          "Armbars"),
        Map.entry(TechniqueFamily.SHOULDER_LOCKS,   "Kimura / Americana"),
        Map.entry(TechniqueFamily.LEG_LOCKS,        "Leg Locks"),
        Map.entry(TechniqueFamily.TAKEDOWNS,        "Derribos"),
        Map.entry(TechniqueFamily.SWEEPS,           "Barridas"),
        Map.entry(TechniqueFamily.BACK_TAKES,       "Tomas de Espalda"),
        Map.entry(TechniqueFamily.ESCAPES,          "Escapadas"),
        Map.entry(TechniqueFamily.OTHER,            "Otras")
    );

    // Familias del radar BJJ (excluimos OTHER por ruido)
    private static final List<TechniqueFamily> BJJ_RADAR_FAMILIES = List.of(
        TechniqueFamily.CLOSED_GUARD,
        TechniqueFamily.HALF_GUARD,
        TechniqueFamily.OPEN_GUARD,
        TechniqueFamily.DLR_GUARD,
        TechniqueFamily.BUTTERFLY_GUARD,
        TechniqueFamily.LEG_ENTANGLEMENT,
        TechniqueFamily.GUARD_PASSES,
        TechniqueFamily.CHOKES,
        TechniqueFamily.GUILLOTINES,
        TechniqueFamily.TRIANGLES,
        TechniqueFamily.ARMBARS,
        TechniqueFamily.SHOULDER_LOCKS,
        TechniqueFamily.LEG_LOCKS,
        TechniqueFamily.TAKEDOWNS,
        TechniqueFamily.SWEEPS,
        TechniqueFamily.BACK_TAKES,
        TechniqueFamily.ESCAPES
    );

    private static final Map<String, String> PHYSICAL_LABELS = Map.of(
        "STRENGTH",    "Fuerza",
        "CARDIO",      "Cardio",
        "FLEXIBILITY", "Flexibilidad",
        "HIIT",        "Explosividad",
        "OTHER",       "General"
    );

    private static final List<String> PHYSICAL_RADAR_TYPES = List.of(
        "STRENGTH", "CARDIO", "FLEXIBILITY", "HIIT", "OTHER"
    );

    @GetMapping("/radar/fisico")
    public List<RadarDataPoint> fisicoRadar(@RequestParam(defaultValue = "90") int days) {
        Long ownerId = currentOwner.id();

        String sql = """
            SELECT ps.session_type, COUNT(*) as total
            FROM physical_session ps
            WHERE ps.owner_id = :ownerId
              AND ps.deleted_at IS NULL
              AND ps.session_date >= CURRENT_DATE - :days
            GROUP BY ps.session_type
            """;

        Map<String, Long> counts = jdbcClient.sql(sql)
            .param("ownerId", ownerId)
            .param("days", days)
            .query((rs, _) -> Map.entry(rs.getString("session_type"), rs.getLong("total")))
            .list()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return PHYSICAL_RADAR_TYPES.stream()
            .map(type -> new RadarDataPoint(
                type,
                PHYSICAL_LABELS.get(type),
                counts.getOrDefault(type, 0L)
            ))
            .toList();
    }

    @GetMapping("/radar/bjj")
    public List<RadarDataPoint> bjjRadar(@RequestParam(defaultValue = "90") int days) {
        Long ownerId = currentOwner.id();

        // Cuenta cuántas veces se ha trabajado cada familia en los últimos N días
        String sql = """
            SELECT t.family, COUNT(*) as total
            FROM training_session_technique tst
            JOIN training_session ts ON ts.id = tst.training_session_id
            JOIN technique t ON t.id = tst.technique_id
            WHERE ts.owner_id = :ownerId
              AND ts.deleted_at IS NULL
              AND t.deleted_at IS NULL
              AND t.family IS NOT NULL
              AND ts.session_date >= CURRENT_DATE - :days
            GROUP BY t.family
            """;

        Map<String, Long> counts = jdbcClient.sql(sql)
            .param("ownerId", ownerId)
            .param("days", days)
            .query((rs, _) -> Map.entry(rs.getString("family"), rs.getLong("total")))
            .list()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return BJJ_RADAR_FAMILIES.stream()
            .map(family -> new RadarDataPoint(
                family.name(),
                FAMILY_LABELS.get(family),
                counts.getOrDefault(family.name(), 0L)
            ))
            .toList();
    }
}
