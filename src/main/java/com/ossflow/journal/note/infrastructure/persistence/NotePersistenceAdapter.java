package com.ossflow.journal.note.infrastructure.persistence;

import com.ossflow.journal.note.application.port.NoteRepositoryPort;
import com.ossflow.journal.note.domain.Note;
import com.ossflow.journal.tag.infrastructure.persistence.TagEntity;
import com.ossflow.journal.tag.infrastructure.persistence.TagJpaRepository;
import com.ossflow.shared.exception.NotFoundException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NotePersistenceAdapter implements NoteRepositoryPort {

    private final NoteJpaRepository repository;
    private final TagJpaRepository tagRepository;
    private final NotePersistenceMapper mapper;
    private final EntityManager em;

    @Override
    @Transactional
    public Note save(Note note) {
        NoteEntity entity = note.id() == null
                ? mapper.toEntity(note)
                : repository.findByIdAndOwnerId(note.id(), note.ownerId())
                    .orElseThrow(() -> new NotFoundException("NOTE_NOT_FOUND",
                            "No existe la nota con id %d".formatted(note.id()),
                            Map.of("noteId", note.id())));
        if (note.id() != null) {
            mapper.updateEntity(note, entity);
        }
        if (entity.getOwnerId() == null) entity.setOwnerId(note.ownerId());

        // Resolve tags
        if (note.tags() != null) {
            List<TagEntity> tagEntities = new ArrayList<>();
            for (String tagName : note.tags()) {
                TagEntity tagEntity = tagRepository.findByName(tagName)
                        .orElseGet(() -> {
                            TagEntity t = new TagEntity();
                            t.setName(tagName);
                            return tagRepository.save(t);
                        });
                tagEntities.add(tagEntity);
            }
            entity.setTags(tagEntities);
        }

        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Note> findById(Long id, Long ownerId) {
        return repository.findByIdAndOwnerId(id, ownerId).map(mapper::toDomain);
    }

    @Override
    public Page<Note> findAll(Long ownerId, String targetType, Long targetId, String tag, String q, Pageable pageable) {
        StringBuilder jpql = new StringBuilder(
                "SELECT DISTINCT n FROM NoteEntity n LEFT JOIN n.tags t WHERE n.ownerId = :ownerId");
        if (targetType != null && !targetType.isBlank()) {
            jpql.append(" AND n.targetType = :targetType");
        }
        if (targetId != null) {
            jpql.append(" AND n.targetId = :targetId");
        }
        if (tag != null && !tag.isBlank()) {
            jpql.append(" AND t.name = :tag");
        }
        if (q != null && !q.isBlank()) {
            jpql.append(" AND (LOWER(n.title) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(n.bodyMarkdown) LIKE LOWER(CONCAT('%', :q, '%')))");
        }
        jpql.append(" ORDER BY n.createdAt DESC");

        var query = em.createQuery(jpql.toString(), NoteEntity.class);
        query.setParameter("ownerId", ownerId);
        if (targetType != null && !targetType.isBlank()) query.setParameter("targetType", targetType);
        if (targetId != null) query.setParameter("targetId", targetId);
        if (tag != null && !tag.isBlank()) query.setParameter("tag", tag);
        if (q != null && !q.isBlank()) query.setParameter("q", q);

        var countJpql = jpql.toString()
                .replace("SELECT DISTINCT n FROM", "SELECT COUNT(DISTINCT n) FROM")
                .replace(" ORDER BY n.createdAt DESC", "");
        var countQuery = em.createQuery(countJpql, Long.class);
        countQuery.setParameter("ownerId", ownerId);
        if (targetType != null && !targetType.isBlank()) countQuery.setParameter("targetType", targetType);
        if (targetId != null) countQuery.setParameter("targetId", targetId);
        if (tag != null && !tag.isBlank()) countQuery.setParameter("tag", tag);
        if (q != null && !q.isBlank()) countQuery.setParameter("q", q);

        long total = countQuery.getSingleResult();
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<NoteEntity> results = query.getResultList();

        return new PageImpl<>(results.stream().map(mapper::toDomain).toList(), pageable, total);
    }

    @Override
    @Transactional
    public void softDelete(Long id, Long ownerId) {
        var entity = repository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new NotFoundException("NOTE_NOT_FOUND",
                        "No existe la nota con id %d".formatted(id), Map.of("noteId", id)));
        entity.softDelete(Instant.now(), Duration.ofDays(30));
        repository.save(entity);
    }

    @Override
    @Transactional
    public Note restore(Long id, Long ownerId) {
        var query = em.createNativeQuery(
                "UPDATE note SET deleted_at = NULL, purge_at = NULL, updated_at = ?1 WHERE id = ?2 AND owner_id = ?3 AND deleted_at IS NOT NULL");
        query.setParameter(1, Instant.now().toString());
        query.setParameter(2, id);
        query.setParameter(3, ownerId);
        int updated = query.executeUpdate();
        if (updated == 0) {
            throw new NotFoundException("NOTE_NOT_FOUND",
                    "Nota no encontrada en papelera", Map.of("noteId", id));
        }
        em.clear();
        return findById(id, ownerId).orElseThrow();
    }

    @Override
    public Page<Note> findTrash(Long ownerId, Pageable pageable) {
        var countQuery = em.createNativeQuery(
                "SELECT COUNT(*) FROM note WHERE owner_id = ?1 AND deleted_at IS NOT NULL");
        countQuery.setParameter(1, ownerId);
        long total = ((Number) countQuery.getSingleResult()).longValue();

        var listQuery = em.createNativeQuery(
                "SELECT * FROM note WHERE owner_id = ?1 AND deleted_at IS NOT NULL ORDER BY deleted_at DESC LIMIT ?2 OFFSET ?3",
                NoteEntity.class);
        listQuery.setParameter(1, ownerId);
        listQuery.setParameter(2, pageable.getPageSize());
        listQuery.setParameter(3, pageable.getOffset());
        @SuppressWarnings("unchecked")
        var list = (List<NoteEntity>) listQuery.getResultList();
        return new PageImpl<>(list.stream().map(mapper::toDomain).toList(), pageable, total);
    }

    @Override
    public long countByTagId(Long tagId) {
        var query = em.createNativeQuery(
                "SELECT COUNT(*) FROM note_tag WHERE tag_id = ?1");
        query.setParameter(1, tagId);
        return ((Number) query.getSingleResult()).longValue();
    }
}
