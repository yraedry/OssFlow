package com.ossflow.catalog.system.application;

import com.ossflow.catalog.system.application.port.SystemRepositoryPort;
import com.ossflow.catalog.system.application.validation.FlowReferentialValidationStep;
import com.ossflow.catalog.system.application.validation.FlowSchemaValidationStep;
import com.ossflow.catalog.system.application.validation.FlowSemanticValidationStep;
import com.ossflow.catalog.system.domain.OssSystem;
import com.ossflow.shared.exception.DuplicateNameException;
import com.ossflow.shared.exception.JsonSchemaViolationException;
import com.ossflow.shared.exception.NotFoundException;
import com.ossflow.shared.validation.ValidationChain;
import com.ossflow.shared.validation.ValidationResult;
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
public class SystemService {

    private final SystemRepositoryPort repository;
    private final FlowSchemaValidationStep schemaStep;
    private final FlowSemanticValidationStep semanticStep;
    private final FlowReferentialValidationStep referentialStep;

    public OssSystem create(OssSystem system) {
        validateFlow(system);
        if (repository.existsByName(system.ownerId(), system.name())) {
            throw new DuplicateNameException("SYSTEM_NAME_DUPLICATE",
                    "Ya existe un sistema con el nombre '%s'".formatted(system.name()),
                    Map.of("name", system.name()));
        }
        OssSystem saved = repository.save(system);
        log.info("Sistema creado id={} name={}", saved.id(), saved.name());
        return saved;
    }

    public OssSystem findById(Long id, Long ownerId) {
        return repository.findById(id, ownerId)
                .orElseThrow(() -> new NotFoundException("SYSTEM_NOT_FOUND",
                        "No existe el sistema con id %d".formatted(id),
                        Map.of("systemId", id)));
    }

    public Page<OssSystem> list(Long ownerId, Pageable pageable) {
        return repository.findAll(ownerId, pageable);
    }

    public OssSystem replace(Long id, Long ownerId, OssSystem replacement) {
        OssSystem existing = findById(id, ownerId);
        if (!existing.name().equals(replacement.name())
                && repository.existsByName(ownerId, replacement.name())) {
            throw new DuplicateNameException("SYSTEM_NAME_DUPLICATE",
                    "Ya existe un sistema con el nombre '%s'".formatted(replacement.name()),
                    Map.of("name", replacement.name()));
        }
        validateFlow(replacement.toBuilder().ownerId(ownerId).build());
        return repository.save(replacement.toBuilder()
                .id(existing.id()).ownerId(existing.ownerId())
                .createdAt(existing.createdAt()).version(existing.version()).build());
    }

    public void softDelete(Long id, Long ownerId) {
        repository.softDelete(id, ownerId);
        log.info("Sistema soft-deleted id={}", id);
    }

    public OssSystem restore(Long id, Long ownerId) {
        OssSystem restored = repository.restore(id, ownerId);
        log.info("Sistema restaurado id={}", id);
        return restored;
    }

    public Page<OssSystem> trash(Long ownerId, Pageable pageable) {
        return repository.findTrash(ownerId, pageable);
    }

    private void validateFlow(OssSystem system) {
        var chain = new ValidationChain<>(List.of(schemaStep, semanticStep, referentialStep));
        ValidationResult result = chain.run(system);
        if (result instanceof ValidationResult.Fail fail) {
            throw new JsonSchemaViolationException(fail.errorCode(), fail.message(), fail.details());
        }
    }
}
