package com.ossflow.technique.application.service;

import com.ossflow.technique.application.port.in.CreateTechniqueUseCase;
import com.ossflow.technique.application.port.out.PositionRepositoryPort;
import com.ossflow.technique.application.port.out.TechniqueRepositoryPort;
import com.ossflow.technique.domain.model.Position;
import com.ossflow.technique.domain.model.Technique;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TechniqueService implements CreateTechniqueUseCase {

    private final TechniqueRepositoryPort techniqueRepository;
    private final PositionRepositoryPort positionRepository; // Inyectamos el puerto de posiciones

    @Override
    public Technique create(Technique technique, Long startPositionId) {
        if (startPositionId != null) {
            // Buscamos la posición. Si no existe, lanzamos un error que detiene el guardado
            Position startPosition = positionRepository.findById(startPositionId)
                    .orElseThrow(() -> new IllegalArgumentException("La posición de origen no existe"));

            // Enlazamos los nodos en el dominio puro
            technique.setStartPosition(startPosition);
        }

        return techniqueRepository.save(technique);
    }
}