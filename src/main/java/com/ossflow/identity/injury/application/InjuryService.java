package com.ossflow.identity.injury.application;

import com.ossflow.identity.injury.application.port.InjuryRepositoryPort;
import com.ossflow.identity.injury.domain.Injury;
import com.ossflow.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class InjuryService {

    private final InjuryRepositoryPort repository;

    public List<Injury> findAll(Long ownerId) {
        return repository.findAllByOwnerId(ownerId);
    }

    public Injury create(Injury injury) {
        Injury saved = repository.save(injury);
        log.info("Lesión creada id={} bodyPart={}", saved.id(), saved.bodyPart());
        return saved;
    }

    public Injury update(Long id, Long ownerId, Injury replacement) {
        Injury existing = repository.findById(id, ownerId)
                .orElseThrow(() -> new NotFoundException("INJURY_NOT_FOUND",
                        "No existe la lesión con id %d".formatted(id),
                        Map.of("injuryId", id)));
        Injury updated = repository.save(replacement.toBuilder()
                .id(existing.id())
                .ownerId(existing.ownerId())
                .createdAt(existing.createdAt())
                .version(existing.version())
                .build());
        log.info("Lesión actualizada id={}", updated.id());
        return updated;
    }

    public void delete(Long id, Long ownerId) {
        repository.findById(id, ownerId)
                .orElseThrow(() -> new NotFoundException("INJURY_NOT_FOUND",
                        "No existe la lesión con id %d".formatted(id),
                        Map.of("injuryId", id)));
        repository.deleteByIdAndOwnerId(id, ownerId);
        log.info("Lesión eliminada id={}", id);
    }
}
