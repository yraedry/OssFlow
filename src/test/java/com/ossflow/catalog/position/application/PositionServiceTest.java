package com.ossflow.catalog.position.application;

import com.ossflow.catalog.position.application.port.PositionRepositoryPort;
import com.ossflow.catalog.position.domain.Position;
import com.ossflow.catalog.position.domain.PositionType;
import com.ossflow.catalog.position.domain.Visibility;
import com.ossflow.shared.exception.DuplicateNameException;
import com.ossflow.shared.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PositionServiceTest {

    @Mock PositionRepositoryPort repository;
    @InjectMocks PositionService service;

    @Test
    void should_create_position_when_name_is_unique() {
        var input = Position.builder().ownerId(1L).name("X").type(PositionType.TOP).visibility(Visibility.PRIVATE).build();
        given(repository.existsByName(1L, "X")).willReturn(false);
        given(repository.save(input)).willReturn(input.toBuilder().id(10L).build());

        var result = service.create(input);

        assertThat(result.id()).isEqualTo(10L);
    }

    @Test
    void should_throw_DuplicateNameException_when_creating_duplicate() {
        var input = Position.builder().ownerId(1L).name("X").type(PositionType.TOP).visibility(Visibility.PRIVATE).build();
        given(repository.existsByName(1L, "X")).willReturn(true);

        assertThatThrownBy(() -> service.create(input))
                .isInstanceOf(DuplicateNameException.class)
                .hasFieldOrPropertyWithValue("errorCode", "POSITION_NAME_DUPLICATE");
    }

    @Test
    void should_throw_NotFoundException_when_findById_misses() {
        given(repository.findById(99L, 1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", "POSITION_NOT_FOUND");
    }
}
