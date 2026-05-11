package com.ossflow.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ossflow.catalog.position.domain.Visibility;
import com.ossflow.catalog.position.infrastructure.web.dto.CreatePositionRequest;
import com.ossflow.catalog.technique.domain.Belt;
import com.ossflow.catalog.technique.domain.Modality;
import com.ossflow.catalog.technique.domain.TechniqueCategory;
import com.ossflow.catalog.technique.infrastructure.web.dto.CreateTechniqueRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ossflow.catalog.position.domain.PositionType;
import com.ossflow.testsupport.TestSecurityContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CatalogIntegrationTest {

    @Autowired WebApplicationContext wac;

    MockMvc mvc;
    final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        TestSecurityContext.setOwner(1L);
    }

    @AfterEach
    void tearDownAuth() { TestSecurityContext.clear(); }

    @Test
    void should_create_position_then_technique_referencing_it() throws Exception {
        // create position
        var posReq = new CreatePositionRequest("Guardia Cerrada", PositionType.BOTTOM, null, null, Visibility.PRIVATE);
        String posBody = mvc.perform(post("/api/v1/catalog/positions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(posReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long positionId = objectMapper.readTree(posBody).get("id").asLong();

        // create technique referencing the position
        var techReq = new CreateTechniqueRequest(
                "Armbar", TechniqueCategory.SUBMISSION, null, null, null,
                Belt.WHITE, Modality.GI, positionId, null, Visibility.PRIVATE);
        mvc.perform(post("/api/v1/catalog/techniques")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(techReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.startPositionId").value(positionId));
    }

    @Test
    void should_return_404_when_technique_startPositionId_unknown() throws Exception {
        var req = new CreateTechniqueRequest(
                "Armbar", TechniqueCategory.SUBMISSION, null, null, null,
                Belt.WHITE, Modality.GI, 9999L, null, Visibility.PRIVATE);

        mvc.perform(post("/api/v1/catalog/techniques")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("POSITION_NOT_FOUND"));
    }

    @Test
    void should_soft_delete_and_restore_technique() throws Exception {
        // create position
        var posReq = new CreatePositionRequest("Monte", PositionType.TOP, null, null, Visibility.PRIVATE);
        String posBody = mvc.perform(post("/api/v1/catalog/positions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(posReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long positionId = objectMapper.readTree(posBody).get("id").asLong();

        // create technique
        var techReq = new CreateTechniqueRequest(
                "Kimura", TechniqueCategory.SUBMISSION, null, null, null,
                Belt.WHITE, Modality.GI, positionId, null, Visibility.PRIVATE);
        String techBody = mvc.perform(post("/api/v1/catalog/techniques")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(techReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long techId = objectMapper.readTree(techBody).get("id").asLong();

        // soft delete
        mvc.perform(delete("/api/v1/catalog/techniques/" + techId))
                .andExpect(status().isNoContent());

        // should not be found in normal list
        mvc.perform(get("/api/v1/catalog/techniques/" + techId))
                .andExpect(status().isNotFound());

        // restore
        mvc.perform(post("/api/v1/catalog/techniques/" + techId + "/restore"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(techId));
    }
}
