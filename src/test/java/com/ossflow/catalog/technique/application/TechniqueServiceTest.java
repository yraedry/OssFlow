package com.ossflow.catalog.technique.application;

import com.ossflow.catalog.position.application.port.PositionRepositoryPort;
import com.ossflow.catalog.position.domain.Position;
import com.ossflow.catalog.technique.application.port.TechniqueRepositoryPort;
import com.ossflow.catalog.technique.domain.Belt;
import com.ossflow.catalog.technique.domain.Modality;
import com.ossflow.catalog.technique.domain.Technique;
import com.ossflow.catalog.technique.domain.TechniqueCategory;
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
class TechniqueServiceTest {

    @Mock TechniqueRepositoryPort repository;
    @Mock PositionRepositoryPort positionRepository;
    @InjectMocks TechniqueService service;

    private Technique baseInput() {
        return Technique.builder()
                .ownerId(1L).name("Armbar").category(TechniqueCategory.SUBMISSION)
                .minimumBelt(Belt.WHITE).modality(Modality.GI)
                .startPositionId(1L).visibility(Visibility.PRIVATE).build();
    }

    @Test
    void should_create_technique_when_valid() {
        var input = baseInput();
        given(positionRepository.findById(1L, 1L))
                .willReturn(Optional.of(Position.builder().id(1L).build()));
        given(repository.existsByName(1L, "Armbar")).willReturn(false);
        given(repository.save(input)).willReturn(input.toBuilder().id(5L).build());

        var result = service.create(input);

        assertThat(result.id()).isEqualTo(5L);
    }

    @Test
    void should_throw_NotFoundException_when_startPositionId_invalid() {
        var input = baseInput();
        given(positionRepository.findById(1L, 1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(input))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", "POSITION_NOT_FOUND");
    }

    @Test
    void should_throw_DuplicateNameException_when_name_taken() {
        var input = baseInput();
        given(positionRepository.findById(1L, 1L))
                .willReturn(Optional.of(Position.builder().id(1L).build()));
        given(repository.existsByName(1L, "Armbar")).willReturn(true);

        assertThatThrownBy(() -> service.create(input))
                .isInstanceOf(DuplicateNameException.class)
                .hasFieldOrPropertyWithValue("errorCode", "TECHNIQUE_NAME_DUPLICATE");
    }

    @Test
    void should_throw_NotFoundException_when_findById_misses() {
        given(repository.findById(99L, 1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", "TECHNIQUE_NOT_FOUND");
    }
}
