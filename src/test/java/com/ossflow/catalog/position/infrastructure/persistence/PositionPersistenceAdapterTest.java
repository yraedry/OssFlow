package com.ossflow.catalog.position.infrastructure.persistence;

import com.ossflow.catalog.position.domain.Position;
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
class PositionPersistenceAdapterTest {

    @Autowired
    PositionPersistenceAdapter adapter;

    @Test
    void should_save_find_and_softDelete_position() {
        var saved = adapter.save(Position.builder()
                .ownerId(1L)
                .name("Guardia Cerrada")
                .type(PositionType.BOTTOM)
                .visibility(Visibility.PRIVATE)
                .build());

        assertThat(saved.id()).isNotNull();
        assertThat(adapter.findById(saved.id(), 1L)).isPresent();

        adapter.softDelete(saved.id(), 1L);

        assertThat(adapter.findById(saved.id(), 1L)).isEmpty();
        assertThat(adapter.findInTrashById(saved.id(), 1L)).isPresent();
    }

    @Test
    void should_restore_position_from_trash() {
        var saved = adapter.save(Position.builder()
                .ownerId(1L).name("Montada").type(PositionType.TOP).visibility(Visibility.PRIVATE).build());
        adapter.softDelete(saved.id(), 1L);

        var restored = adapter.restore(saved.id(), 1L);

        assertThat(restored.deletedAt()).isNull();
        assertThat(adapter.findById(saved.id(), 1L)).isPresent();
    }

    @Test
    void should_filter_trash_by_owner() {
        var saved = adapter.save(Position.builder()
                .ownerId(1L).name("Espalda").type(PositionType.TOP).visibility(Visibility.PRIVATE).build());
        adapter.softDelete(saved.id(), 1L);

        var trash = adapter.findTrash(1L, PageRequest.of(0, 10));

        assertThat(trash.getTotalElements()).isEqualTo(1);
    }
}
