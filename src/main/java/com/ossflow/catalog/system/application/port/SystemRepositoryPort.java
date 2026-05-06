package com.ossflow.catalog.system.application.port;

import com.ossflow.catalog.system.domain.OssSystem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SystemRepositoryPort {
    OssSystem save(OssSystem system);
    Optional<OssSystem> findById(Long id, Long ownerId);
    Page<OssSystem> findAll(Long ownerId, Pageable pageable);
    boolean existsByName(Long ownerId, String name);
    void softDelete(Long id, Long ownerId);
    OssSystem restore(Long id, Long ownerId);
    Page<OssSystem> findTrash(Long ownerId, Pageable pageable);
}
