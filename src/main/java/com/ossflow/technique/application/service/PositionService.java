package com.ossflow.technique.application.service;

import com.ossflow.technique.application.port.in.CreatePositionUseCase;
import com.ossflow.technique.application.port.in.GetPositionsUseCase;
import com.ossflow.technique.application.port.out.PositionRepositoryPort;
import com.ossflow.technique.domain.model.Position;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionService implements CreatePositionUseCase, GetPositionsUseCase {

    private final PositionRepositoryPort port;

    @Override
    public Position create(Position position) {
        return port.save(position);
    }

    @Override
    public List<Position> getAll() {
        return port.findAll();
    }

    public List<Position> searchByName(String name) {
        return port.findByName(name);
    }
}