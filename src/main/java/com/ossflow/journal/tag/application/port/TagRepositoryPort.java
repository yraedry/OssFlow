package com.ossflow.journal.tag.application.port;

import com.ossflow.journal.tag.domain.Tag;

import java.util.List;

public interface TagRepositoryPort {
    List<Tag> findByNamePrefix(String prefix, int limit);
    Tag findOrCreate(String name);
    void deleteById(Long id);
    List<Tag> findAll();
}
