package com.ossflow.catalog.position.infrastructure.persistence;

import com.ossflow.catalog.position.domain.PositionType;
import com.ossflow.catalog.position.domain.Visibility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
class PositionJpaRepositoryTest {

    @Autowired
    PositionJpaRepository repository;

    @Test
    void should_persist_and_retrieve_position() {
        var entity = PositionEntity.builder()
                .name("Guardia Cerrada")
                .type(PositionType.BOTTOM)
                .visibility(Visibility.PRIVATE)
                .build();
        entity.setOwnerId(1L);

        var saved = repository.save(entity);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getVersion()).isZero();
    }

    @Test
    void should_filter_by_name_case_insensitive() {
        var a = PositionEntity.builder().name("Guardia Cerrada").type(PositionType.BOTTOM).visibility(Visibility.PRIVATE).build();
        a.setOwnerId(1L);
        var b = PositionEntity.builder().name("Montada").type(PositionType.TOP).visibility(Visibility.PRIVATE).build();
        b.setOwnerId(1L);
        repository.save(a);
        repository.save(b);

        var page = repository.findByOwnerIdAndNameContainingIgnoreCase(1L, "guard", PageRequest.of(0, 10));

        assertThat(page.getContent()).extracting(PositionEntity::getName).containsExactly("Guardia Cerrada");
    }
}
