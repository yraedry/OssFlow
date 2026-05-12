package com.ossflow.catalog.technique.infrastructure.persistence;

import com.ossflow.catalog.technique.domain.Belt;
import com.ossflow.catalog.technique.domain.Modality;
import com.ossflow.catalog.technique.domain.TechniqueCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TechniqueJpaRepository extends JpaRepository<TechniqueEntity, Long> {

    Optional<TechniqueEntity> findByIdAndOwnerId(Long id, Long ownerId);

    @Query("SELECT t FROM TechniqueEntity t WHERE t.id = :id AND (t.ownerId = :ownerId OR t.visibility = com.ossflow.catalog.position.domain.Visibility.PUBLIC)")
    Optional<TechniqueEntity> findByIdReadable(Long id, Long ownerId);

    @Query("SELECT t FROM TechniqueEntity t WHERE t.ownerId = :ownerId OR t.visibility = com.ossflow.catalog.position.domain.Visibility.PUBLIC")
    Page<TechniqueEntity> findByOwnerId(Long ownerId, Pageable pageable);

    boolean existsByOwnerIdAndName(Long ownerId, String name);

    long countByStartPositionIdOrEndPositionId(Long startId, Long endId);

    @Query(value = """
            SELECT t.* FROM technique t
            WHERE (t.owner_id = :ownerId OR t.visibility = 'PUBLIC')
            AND t.deleted_at IS NULL
            AND (:category IS NULL OR t.category = CAST(:category AS varchar))
            AND (:belt IS NULL OR t.minimum_belt = CAST(:belt AS varchar))
            AND (:modality IS NULL OR t.modality = CAST(:modality AS varchar))
            AND (:startPositionId IS NULL OR t.start_position_id = :startPositionId)
            AND (:endPositionId IS NULL OR t.end_position_id = :endPositionId)
            AND (:search IS NULL OR unaccent(lower(t.name)) LIKE unaccent(lower(CONCAT('%', :search, '%')))
                                OR unaccent(lower(COALESCE(t.description, ''))) LIKE unaccent(lower(CONCAT('%', :search, '%'))))
            """,
            countQuery = """
            SELECT COUNT(*) FROM technique t
            WHERE (t.owner_id = :ownerId OR t.visibility = 'PUBLIC')
            AND t.deleted_at IS NULL
            AND (:category IS NULL OR t.category = CAST(:category AS varchar))
            AND (:belt IS NULL OR t.minimum_belt = CAST(:belt AS varchar))
            AND (:modality IS NULL OR t.modality = CAST(:modality AS varchar))
            AND (:startPositionId IS NULL OR t.start_position_id = :startPositionId)
            AND (:endPositionId IS NULL OR t.end_position_id = :endPositionId)
            AND (:search IS NULL OR unaccent(lower(t.name)) LIKE unaccent(lower(CONCAT('%', :search, '%')))
                                OR unaccent(lower(COALESCE(t.description, ''))) LIKE unaccent(lower(CONCAT('%', :search, '%'))))
            """,
            nativeQuery = true)
    Page<TechniqueEntity> findByFilters(Long ownerId, String category, String belt, String modality,
                                         Long startPositionId, Long endPositionId, String search,
                                         Pageable pageable);
}
