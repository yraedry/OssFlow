package com.ossflow.technique.application.port.in;

import com.ossflow.technique.domain.model.Position;

import java.util.List;

public interface GetPositionsUseCase {
    List<Position> getAll();
}