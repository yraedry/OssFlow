package com.ossflow.catalog.web;

import com.ossflow.catalog.position.application.PositionService;
import com.ossflow.catalog.position.domain.Position;
import com.ossflow.catalog.position.domain.PositionType;
import com.ossflow.catalog.position.domain.Visibility;
import com.ossflow.catalog.position.infrastructure.web.PositionWebMapperImpl;
import com.ossflow.catalog.technique.application.TechniqueService;
import com.ossflow.catalog.technique.domain.Technique;
import com.ossflow.catalog.technique.infrastructure.web.TechniqueWebMapperImpl;
import com.ossflow.shared.exception.GlobalExceptionHandler;
import com.ossflow.shared.web.CurrentOwner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CatalogTrashControllerTest {

    @Mock PositionService positionService;
    @Mock TechniqueService techniqueService;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        var positionMapper = new PositionWebMapperImpl();
        var techniqueMapper = new TechniqueWebMapperImpl();
        var currentOwner = new CurrentOwner();
        var controller = new CatalogTrashController(
                positionService, positionMapper, techniqueService, techniqueMapper, currentOwner);
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void should_return_paged_trash_positions_when_called() throws Exception {
        var position = Position.builder()
                .id(5L).ownerId(1L).name("Guardia").type(PositionType.BOTTOM).visibility(Visibility.PRIVATE)
                .build();
        given(positionService.trash(eq(1L), any())).willReturn(
                new PageImpl<>(List.of(position), PageRequest.of(0, 20), 1));

        mvc.perform(get("/api/v1/catalog/trash/positions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(5))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void should_return_empty_page_when_no_trash_positions() throws Exception {
        given(positionService.trash(eq(1L), any())).willReturn(
                new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));

        mvc.perform(get("/api/v1/catalog/trash/positions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void should_return_paged_trash_techniques_when_called() throws Exception {
        var technique = Technique.builder()
                .id(8L).ownerId(1L).name("Armbar").visibility(Visibility.PRIVATE)
                .build();
        given(techniqueService.trash(eq(1L), any())).willReturn(
                new PageImpl<>(List.of(technique), PageRequest.of(0, 20), 1));

        mvc.perform(get("/api/v1/catalog/trash/techniques"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(8))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void should_cap_page_size_at_100_when_larger_size_requested() throws Exception {
        given(positionService.trash(eq(1L), any())).willReturn(
                new PageImpl<>(List.of(), PageRequest.of(0, 100), 0));

        mvc.perform(get("/api/v1/catalog/trash/positions")
                        .param("size", "500"))
                .andExpect(status().isOk());
    }
}
