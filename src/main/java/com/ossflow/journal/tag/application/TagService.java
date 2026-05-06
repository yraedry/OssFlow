package com.ossflow.journal.tag.application;

import com.ossflow.journal.tag.application.port.TagRepositoryPort;
import com.ossflow.journal.tag.domain.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepositoryPort repository;

    public List<Tag> findByNamePrefix(String prefix, int limit) {
        return repository.findByNamePrefix(prefix, limit);
    }

    public Tag findOrCreate(String name) {
        Tag tag = repository.findOrCreate(name);
        log.info("Tag findOrCreate name={} id={}", tag.name(), tag.id());
        return tag;
    }

    public List<Tag> findAll() {
        return repository.findAll();
    }

    public void delete(Long id) {
        repository.deleteById(id);
        log.info("Tag deleted id={}", id);
    }
}
