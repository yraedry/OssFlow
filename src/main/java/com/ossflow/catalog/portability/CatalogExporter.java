package com.ossflow.catalog.portability;

import com.ossflow.catalog.position.application.PositionService;
import com.ossflow.catalog.technique.application.TechniqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CatalogExporter {

    private final PositionService positionService;
    private final TechniqueService techniqueService;

    public Map<String, Object> exportFor(Long ownerId) {
        Pageable all = PageRequest.of(0, Integer.MAX_VALUE);
        var positions = positionService.list(ownerId, null, all).getContent();
        var techniques = techniqueService.list(ownerId, null, null, null, null, null, null, all).getContent();
        return Map.of(
                "positions", positions,
                "techniques", techniques
        );
    }
}
