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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
class PositionRepositoryVisibilityTest {

    @Autowired
    PositionPersistenceAdapter adapter;

    @Test
    void user_sees_own_private_positions() {
        Position own = adapter.save(Position.builder()
                .ownerId(7L).name("Mi Guardia Privada")
                .type(PositionType.BOTTOM).visibility(Visibility.PRIVATE).build());

        var page = adapter.findAll(7L, null, PageRequest.of(0, 50));

        assertThat(page.getContent()).extracting(Position::id).contains(own.id());
    }

    @Test
    void user_sees_system_catalog_via_PUBLIC_visibility_not_owned() {
        Position systemCatalog = adapter.save(Position.builder()
                .ownerId(1L).name("Catalogo Sistema X")
                .type(PositionType.BOTTOM).visibility(Visibility.PUBLIC).build());

        var page = adapter.findAll(99L, null, PageRequest.of(0, 50));

        assertThat(page.getContent()).extracting(Position::id).contains(systemCatalog.id());
    }

    @Test
    void user_does_not_see_other_users_PRIVATE_positions() {
        Position other = adapter.save(Position.builder()
                .ownerId(1L).name("Tecnica privada user 1")
                .type(PositionType.BOTTOM).visibility(Visibility.PRIVATE).build());

        var page = adapter.findAll(99L, null, PageRequest.of(0, 50));

        List<Long> visibleIds = page.getContent().stream().map(Position::id).toList();
        assertThat(visibleIds).doesNotContain(other.id());
    }
}
