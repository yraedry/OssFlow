package com.ossflow.technique.application.port.in;

import com.ossflow.technique.domain.model.Technique;

public interface CreateTechniqueUseCase {
    Technique create(Technique technique, Long startPositionId);
}