package com.ossflow.identity.injury.application.port;

import com.ossflow.identity.injury.domain.Injury;

import java.util.List;
import java.util.Optional;

public interface InjuryRepositoryPort {
    Injury save(Injury injury);
    Optional<Injury> findById(Long id, Long ownerId);
    List<Injury> findAllByOwnerId(Long ownerId);
    void deleteByIdAndOwnerId(Long id, Long ownerId);
}
