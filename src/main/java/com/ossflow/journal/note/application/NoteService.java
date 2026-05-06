package com.ossflow.journal.note.application;

import com.ossflow.journal.note.application.port.NoteRepositoryPort;
import com.ossflow.journal.note.domain.Note;
import com.ossflow.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepositoryPort repository;

    public Note create(Note note) {
        Note saved = repository.save(note);
        log.info("Nota creada id={} title={}", saved.id(), saved.title());
        return saved;
    }

    public Note findById(Long id, Long ownerId) {
        return repository.findById(id, ownerId)
                .orElseThrow(() -> new NotFoundException("NOTE_NOT_FOUND",
                        "No existe la nota con id %d".formatted(id),
                        Map.of("noteId", id)));
    }

    public Page<Note> list(Long ownerId, String targetType, Long targetId, String tag, String q, Pageable pageable) {
        return repository.findAll(ownerId, targetType, targetId, tag, q, pageable);
    }

    public Note replace(Long id, Long ownerId, Note replacement) {
        Note existing = findById(id, ownerId);
        Note toSave = replacement.toBuilder()
                .id(existing.id())
                .ownerId(existing.ownerId())
                .createdAt(existing.createdAt())
                .version(existing.version())
                .build();
        return repository.save(toSave);
    }

    public Note patch(Long id, Long ownerId, Note patched) {
        return repository.save(patched.toBuilder()
                .id(id)
                .ownerId(ownerId)
                .build());
    }

    public void softDelete(Long id, Long ownerId) {
        repository.softDelete(id, ownerId);
        log.info("Nota soft-deleted id={}", id);
    }

    public Note restore(Long id, Long ownerId) {
        Note restored = repository.restore(id, ownerId);
        log.info("Nota restaurada id={}", id);
        return restored;
    }

    public Page<Note> trash(Long ownerId, Pageable pageable) {
        return repository.findTrash(ownerId, pageable);
    }
}
