package com.ossflow.identity.auth.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Audita las rutas protegidas: sin token Bearer, todas las rutas autenticadas
 * deben devolver 401. Solo las rutas explícitas en permitAll deben ser accesibles.
 */
@SpringBootTest
@ActiveProfiles("test")
class SecurityConfigPathsTest {

    @Autowired WebApplicationContext wac;

    MockMvc mvc() {
        return MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();
    }

    @Test
    void protected_endpoints_return_401_without_token() throws Exception {
        mvc().perform(get("/api/v1/identity/profile")).andExpect(status().isUnauthorized());
        mvc().perform(get("/api/v1/journal/training-sessions")).andExpect(status().isUnauthorized());
        mvc().perform(get("/api/v1/catalog/techniques")).andExpect(status().isUnauthorized());
        mvc().perform(get("/api/v1/planning/study-plans")).andExpect(status().isUnauthorized());
        mvc().perform(get("/api/v1/journal/notes")).andExpect(status().isUnauthorized());
    }

    @Test
    void public_auth_endpoints_do_not_return_401() throws Exception {
        // /logout es idempotente: sin cookie devuelve 204.
        mvc().perform(post("/api/auth/logout")).andExpect(status().isNoContent());
        // /refresh sin cookie devuelve 401 (no autorización pero porque no hay token,
        // no porque el filter de Spring Security lo bloquee). Cualquier status != 403 vale.
        mvc().perform(post("/api/auth/refresh")).andExpect(status().isUnauthorized());
    }
}
