package com.ossflow.technique.infra.adapter.out.db.repository;

import com.ossflow.technique.infra.adapter.out.db.entity.PositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PositionJpaRepository extends JpaRepository<PositionEntity, Long> {
    List<PositionEntity> findByNameContainingIgnoreCase(String name);
}