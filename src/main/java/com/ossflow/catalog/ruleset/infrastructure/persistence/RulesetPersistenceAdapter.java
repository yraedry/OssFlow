package com.ossflow.catalog.ruleset.infrastructure.persistence;

import com.ossflow.catalog.ruleset.application.port.RulesetRepositoryPort;
import com.ossflow.catalog.ruleset.domain.Ruleset;
import com.ossflow.catalog.ruleset.domain.RulesetTechnique;
import com.ossflow.catalog.technique.domain.Belt;
import com.ossflow.catalog.technique.domain.Modality;
import com.ossflow.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RulesetPersistenceAdapter implements RulesetRepositoryPort {

    private final RulesetJpaRepository repository;
    private final RulesetTechniqueJpaRepository techniqueRepository;
    private final RulesetPersistenceMapper mapper;
    private final RulesetTechniqueMapper techniqueMapper;

    @Override
    public Ruleset save(Ruleset ruleset) {
        RulesetEntity entity = ruleset.id() == null
                ? mapper.toEntity(ruleset)
                : repository.findById(ruleset.id())
                    .orElseThrow(() -> new NotFoundException("RULESET_NOT_FOUND",
                            "No existe el reglamento con id %d".formatted(ruleset.id()),
                            Map.of("rulesetId", ruleset.id())));
        if (ruleset.id() != null) {
            entity.setFederationId(ruleset.federationId());
            entity.setBelt(ruleset.belt());
            entity.setModality(ruleset.modality());
            entity.setEffectiveFrom(ruleset.effectiveFrom());
            entity.setEffectiveTo(ruleset.effectiveTo());
            entity.setSourceUrl(ruleset.sourceUrl());
        }
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Ruleset> findById(Long id) {
        return repository.findWithFederationById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Ruleset> findByFilters(Long federationId, Belt belt, Modality modality, Pageable pageable) {
        return repository.findByFilters(federationId, belt, modality, pageable).map(mapper::toDomain);
    }

    @Override
    public boolean existsByUniqueKey(Long federationId, Belt belt, Modality modality, LocalDate effectiveFrom) {
        return repository.existsByFederationIdAndBeltAndModalityAndEffectiveFrom(
                federationId, belt, modality, effectiveFrom);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("RULESET_NOT_FOUND",
                    "No existe el reglamento con id %d".formatted(id),
                    Map.of("rulesetId", id));
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public RulesetTechnique upsertTechnique(Long rulesetId, RulesetTechnique technique) {
        var id = new RulesetTechniqueId(rulesetId, technique.techniqueId());
        var entity = techniqueRepository.findById(id).orElseGet(() -> {
            var e = new RulesetTechniqueEntity();
            e.setId(id);
            var rulesetRef = repository.getReferenceById(rulesetId);
            e.setRuleset(rulesetRef);
            return e;
        });
        entity.setStatus(technique.status());
        entity.setConditionNotes(technique.conditionNotes());
        return techniqueMapper.toDomain(techniqueRepository.save(entity));
    }

    @Override
    @Transactional
    public void removeTechnique(Long rulesetId, Long techniqueId) {
        var id = new RulesetTechniqueId(rulesetId, techniqueId);
        if (!techniqueRepository.existsById(id)) {
            throw new NotFoundException("RULESET_TECHNIQUE_NOT_FOUND",
                    "Técnica %d no encontrada en reglamento %d".formatted(techniqueId, rulesetId),
                    Map.of("rulesetId", rulesetId, "techniqueId", techniqueId));
        }
        techniqueRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RulesetTechnique> findTechniquesByTechniqueId(Long techniqueId) {
        return techniqueRepository.findByIdTechniqueId(techniqueId).stream()
                .map(techniqueMapper::toDomain).toList();
    }
}
