package com.ossflow.technique.infra.adapter.in.web;

import com.ossflow.technique.application.port.in.CreateTechniqueUseCase;
import com.ossflow.technique.domain.model.Technique;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ossflow.technique.infra.adapter.in.web.dto.CreateTechniqueRequest;

@RestController
@RequestMapping("/api/v1/techniques")
@RequiredArgsConstructor
public class TechniqueController {

    private final CreateTechniqueUseCase createTechniqueUseCase;

    @PostMapping
    public ResponseEntity<Technique> createTechnique(@RequestBody CreateTechniqueRequest request) {
        // Mapeamos el DTO al modelo de dominio manualmente de forma rápida para este caso
        Technique technique = Technique.builder()
                .name(request.getName())
                .category(request.getCategory())
                .description(request.getDescription())
                .youtubeUrl(request.getYoutubeUrl())
                .minimumBelt(request.getMinimumBelt())
                .modality(request.getModality())
                .build();

        Technique created = createTechniqueUseCase.create(technique, request.getStartPositionId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}