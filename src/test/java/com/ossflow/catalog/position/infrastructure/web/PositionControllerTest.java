package com.ossflow.catalog.position.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ossflow.catalog.position.application.PositionService;
import com.ossflow.catalog.position.domain.Position;
import com.ossflow.catalog.position.domain.PositionType;
import com.ossflow.catalog.position.domain.Visibility;
import com.ossflow.catalog.position.infrastructure.web.dto.CreatePositionRequest;
import com.ossflow.shared.exception.DuplicateNameException;
import com.ossflow.shared.exception.GlobalExceptionHandler;
import com.ossflow.shared.web.CurrentOwner;
import com.ossflow.testsupport.TestSecurityContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PositionControllerTest {

    @Mock PositionService service;

    MockMvc mvc;
    ObjectMapper json = new ObjectMapper();

    @BeforeEach
    void setUp() {
        var mapper = new PositionWebMapperImpl();
        var controller = new PositionController(service, mapper, new CurrentOwner());
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        TestSecurityContext.setOwner(1L);
    }

    @AfterEach
    void tearDownAuth() { TestSecurityContext.clear(); }

    @Test
    void post_should_return_201_with_location_header() throws Exception {
        var req = new CreatePositionRequest("Guardia Cerrada", PositionType.BOTTOM, null, null, Visibility.PRIVATE);
        given(service.create(org.mockito.ArgumentMatchers.any())).willReturn(
                Position.builder().id(7L).ownerId(1L).name("Guardia Cerrada")
                        .type(PositionType.BOTTOM).visibility(Visibility.PRIVATE).build());

        mvc.perform(post("/api/v1/catalog/positions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/catalog/positions/7"))
                .andExpect(jsonPath("$.id").value(7));
    }

    @Test
    void post_should_return_400_when_name_blank() throws Exception {
        var req = new CreatePositionRequest("", PositionType.BOTTOM, null, null, Visibility.PRIVATE);

        mvc.perform(post("/api/v1/catalog/positions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("name"));
    }

    @Test
    void post_should_return_409_when_name_duplicate() throws Exception {
        var req = new CreatePositionRequest("Guardia Cerrada", PositionType.BOTTOM, null, null, Visibility.PRIVATE);
        given(service.create(org.mockito.ArgumentMatchers.any()))
                .willThrow(new DuplicateNameException("POSITION_NAME_DUPLICATE", "duplicado", Map.of()));

        mvc.perform(post("/api/v1/catalog/positions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("POSITION_NAME_DUPLICATE"));
    }
}
