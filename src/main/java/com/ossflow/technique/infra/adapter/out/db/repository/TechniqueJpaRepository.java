package com.ossflow.technique.infra.adapter.out.db.repository;

import com.ossflow.technique.infra.adapter.out.db.entity.TechniqueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TechniqueJpaRepository extends JpaRepository<TechniqueEntity, Long> {
}