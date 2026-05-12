package com.ossflow.integration;

import com.ossflow.testsupport.TestSecurityContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FullExportIntegrationTest {

    @Autowired WebApplicationContext wac;
    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();
        TestSecurityContext.setOwner(1L);
    }

    @AfterEach
    void tearDownAuth() { TestSecurityContext.clear(); }

    @Test
    void should_return_full_export_with_all_sections() throws Exception {
        mvc.perform(get("/api/v1/export/full"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("ossflow-backup-")))
                .andExpect(jsonPath("$.schemaVersion").value("v1"))
                .andExpect(jsonPath("$.catalog").exists())
                .andExpect(jsonPath("$.journal").exists())
                .andExpect(jsonPath("$.planning").exists())
                .andExpect(jsonPath("$.identity").exists());
    }
}
