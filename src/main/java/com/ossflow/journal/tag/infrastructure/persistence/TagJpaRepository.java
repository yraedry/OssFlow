package com.ossflow.journal.tag.infrastructure.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagJpaRepository extends JpaRepository<TagEntity, Long> {

    List<TagEntity> findByNameStartingWithIgnoreCase(String prefix, Pageable pageable);

    Optional<TagEntity> findByName(String name);
}
