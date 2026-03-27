package com.ossflow.technique.application.port.in;

import com.ossflow.technique.domain.model.Position;

public interface CreatePositionUseCase {
    Position create(Position position);
}