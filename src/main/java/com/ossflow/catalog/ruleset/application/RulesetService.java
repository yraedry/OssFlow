package com.ossflow.catalog.ruleset.application;

import com.ossflow.catalog.ruleset.application.port.RulesetRepositoryPort;
import com.ossflow.catalog.ruleset.domain.Ruleset;
import com.ossflow.catalog.ruleset.domain.RulesetTechnique;
import com.ossflow.catalog.technique.domain.Belt;
import com.ossflow.catalog.technique.domain.Modality;
import com.ossflow.shared.exception.ConflictException;
import com.ossflow.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RulesetService {

    private final RulesetRepositoryPort repository;

    public Ruleset create(Ruleset ruleset) {
        if (repository.existsByUniqueKey(ruleset.federationId(), ruleset.belt(),
                ruleset.modality(), ruleset.effectiveFrom())) {
            throw new ConflictException("RULESET_DUPLICATE",
                    "Ya existe un reglamento para esa federación/cinturón/modalidad/fecha",
                    Map.of("federationId", ruleset.federationId()));
        }
        Ruleset saved = repository.save(ruleset);
        log.info("Reglamento creado id={}", saved.id());
        return saved;
    }

    public Ruleset findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("RULESET_NOT_FOUND",
                        "No existe el reglamento con id %d".formatted(id),
                        Map.of("rulesetId", id)));
    }

    public Page<Ruleset> list(Long federationId, Belt belt, Modality modality, Pageable pageable) {
        return repository.findByFilters(federationId, belt, modality, pageable);
    }

    public RulesetTechnique upsertTechnique(Long rulesetId, RulesetTechnique technique) {
        findById(rulesetId);
        return repository.upsertTechnique(rulesetId, technique);
    }

    public void delete(Long id) {
        findById(id);
        repository.delete(id);
        log.info("Reglamento eliminado id={}", id);
    }

    public void removeTechnique(Long rulesetId, Long techniqueId) {
        repository.removeTechnique(rulesetId, techniqueId);
    }

    public List<RulesetTechnique> getLegalityForTechnique(Long techniqueId) {
        return repository.findTechniquesByTechniqueId(techniqueId);
    }
}
