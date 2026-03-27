package com.ossflow.technique.application.service;

import com.ossflow.technique.application.port.out.PositionRepositoryPort;
import com.ossflow.technique.application.port.out.TechniqueRepositoryPort;
import com.ossflow.technique.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TechniqueServiceTest {

    @Mock
    private TechniqueRepositoryPort techniqueRepository;

    @Mock
    private PositionRepositoryPort positionRepository; // Necesitamos mockear también las posiciones

    @InjectMocks
    private TechniqueService techniqueService;

    private Technique techniqueToSave;
    private Technique savedTechnique;
    private Position startPosition;

    @BeforeEach
    void setUp() {
        startPosition = Position.builder()
                .id(1L)
                .name("Guardia Cerrada")
                .type(PositionType.BOTTOM)
                .build();

        techniqueToSave = Technique.builder()
                .name("Triángulo")
                .build();

        savedTechnique = Technique.builder()
                .id(100L)
                .name("Triángulo")
                .startPosition(startPosition) // La técnica devuelta ya tiene la posición
                .build();
    }

    @Test
    @DisplayName("Debe enlazar la posición y guardar la técnica cuando el ID de posición existe")
    void shouldLinkPositionAndSaveTechnique() {
        // Given
        Long positionId = 1L;
        when(positionRepository.findById(positionId)).thenReturn(Optional.of(startPosition));
        when(techniqueRepository.save(any(Technique.class))).thenReturn(savedTechnique);

        // When
        Technique result = techniqueService.create(techniqueToSave, positionId);

        // Then
        assertThat(result.getStartPosition()).isEqualTo(startPosition);
        verify(positionRepository).findById(positionId);
        verify(techniqueRepository).save(techniqueToSave);
    }

    @Test
    @DisplayName("Debe lanzar una excepción si la posición de origen no existe")
    void shouldThrowExceptionWhenPositionNotFound() {
        // Given
        Long nonExistentId = 99L;
        when(positionRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> techniqueService.create(techniqueToSave, nonExistentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La posición de origen no existe");
        verify(techniqueRepository, never()).save(any());
    }
}