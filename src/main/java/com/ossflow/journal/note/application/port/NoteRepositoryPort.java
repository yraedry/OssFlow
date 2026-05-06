package com.ossflow.journal.note.application.port;

import com.ossflow.journal.note.domain.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface NoteRepositoryPort {
    Note save(Note note);
    Optional<Note> findById(Long id, Long ownerId);
    Page<Note> findAll(Long ownerId, String targetType, Long targetId, String tag, String q, Pageable pageable);
    void softDelete(Long id, Long ownerId);
    Note restore(Long id, Long ownerId);
    Page<Note> findTrash(Long ownerId, Pageable pageable);
    long countByTagId(Long tagId);
}
