package com.ossflow.catalog.system.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemJpaRepository extends JpaRepository<SystemEntity, Long> {
    Optional<SystemEntity> findByIdAndOwnerId(Long id, Long ownerId);

    @Query("SELECT s FROM SystemEntity s WHERE s.id = :id AND (s.ownerId = :ownerId OR s.visibility = com.ossflow.catalog.position.domain.Visibility.PUBLIC)")
    Optional<SystemEntity> findByIdReadable(Long id, Long ownerId);

    @Query("SELECT s FROM SystemEntity s WHERE s.ownerId = :ownerId OR s.visibility = com.ossflow.catalog.position.domain.Visibility.PUBLIC")
    Page<SystemEntity> findByOwnerId(Long ownerId, Pageable pageable);

    boolean existsByOwnerIdAndName(Long ownerId, String name);
}
