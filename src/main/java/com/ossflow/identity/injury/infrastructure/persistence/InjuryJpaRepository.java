package com.ossflow.identity.injury.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InjuryJpaRepository extends JpaRepository<InjuryEntity, Long> {

    Optional<InjuryEntity> findByIdAndOwnerId(Long id, Long ownerId);

    List<InjuryEntity> findAllByOwnerIdOrderByCreatedAtDesc(Long ownerId);

    void deleteByIdAndOwnerId(Long id, Long ownerId);
}
