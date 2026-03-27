package com.ossflow.technique.application.service;

import com.ossflow.technique.application.port.out.PositionRepositoryPort;
import com.ossflow.technique.domain.model.Position;
import com.ossflow.technique.domain.model.PositionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PositionServiceTest {

    @Mock
    private PositionRepositoryPort repositoryPort;

    @InjectMocks
    private PositionService positionService;

    private Position positionToSave;
    private Position savedPosition;

    @BeforeEach
    void setUp() {
        positionToSave = Position.builder()
                .name("Media Guardia")
                .type(PositionType.BOTTOM)
                .build();

        savedPosition = Position.builder()
                .id(1L)
                .name("Media Guardia")
                .type(PositionType.BOTTOM)
                .build();
    }

    @Test
    @DisplayName("Debe guardar una posición exitosamente llamando al puerto de salida")
    void shouldCreatePositionSuccessfully() {
        // Given
        when(repositoryPort.save(any(Position.class))).thenReturn(savedPosition);

        // When
        Position result = positionService.create(positionToSave);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Media Guardia");
        assertThat(result.getType()).isEqualTo(PositionType.BOTTOM);

        verify(repositoryPort, times(1)).save(any(Position.class));
    }

    @Test
    @DisplayName("Debe filtrar posiciones cuando se busca por un nombre parcial")
    void shouldFilterPositionsByName() {
        // Given
        String searchTerm = "Guardia";
        List<Position> filteredList = List.of(
                Position.builder().name("Guardia Cerrada").build(),
                Position.builder().name("Media Guardia").build()
        );
        when(repositoryPort.findByName(searchTerm)).thenReturn(filteredList);

        // When
        List<Position> result = positionService.searchByName(searchTerm);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(p -> p.getName().contains(searchTerm));
        verify(repositoryPort).findByName(searchTerm);
    }
}