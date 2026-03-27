package com.ossflow.technique.infra.adapter.in.web;

import com.ossflow.technique.application.port.in.CreatePositionUseCase;
import com.ossflow.technique.application.port.in.GetPositionsUseCase;
import com.ossflow.technique.application.service.PositionService;
import com.ossflow.technique.domain.model.Position;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/positions")
@RequiredArgsConstructor
public class PositionController {

    private final CreatePositionUseCase createPositionUseCase;
    private final GetPositionsUseCase getPositionUseCase;
    private final PositionService positionService;

    @PostMapping
    public ResponseEntity<Position> createPosition(@RequestBody Position position) {
        Position created = createPositionUseCase.create(position);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Position>> getPositions(@RequestParam(required = false) String name) {
        // Si el usuario envía ?name=..., filtramos
        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(positionService.searchByName(name));
        }

        // Si no envía nada, devolvemos todo
        return ResponseEntity.ok(getPositionUseCase.getAll());
    }
}