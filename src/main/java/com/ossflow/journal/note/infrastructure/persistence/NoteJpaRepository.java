package com.ossflow.journal.note.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoteJpaRepository extends JpaRepository<NoteEntity, Long> {

    Optional<NoteEntity> findByIdAndOwnerId(Long id, Long ownerId);

    Page<NoteEntity> findByOwnerIdOrderByCreatedAtDesc(Long ownerId, Pageable pageable);

    boolean existsByOwnerIdAndTitle(Long ownerId, String title);
}
