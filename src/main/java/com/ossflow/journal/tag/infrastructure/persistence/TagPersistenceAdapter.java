package com.ossflow.journal.tag.infrastructure.persistence;

import com.ossflow.journal.tag.application.port.TagRepositoryPort;
import com.ossflow.journal.tag.domain.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TagPersistenceAdapter implements TagRepositoryPort {

    private final TagJpaRepository repository;
    private final TagPersistenceMapper mapper;

    @Override
    public List<Tag> findByNamePrefix(String prefix, int limit) {
        return repository.findByNameStartingWithIgnoreCase(prefix, PageRequest.of(0, limit))
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public Tag findOrCreate(String name) {
        return repository.findByName(name)
                .map(mapper::toDomain)
                .orElseGet(() -> {
                    TagEntity entity = new TagEntity();
                    entity.setName(name);
                    return mapper.toDomain(repository.save(entity));
                });
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Tag> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }
}
