package com.ossflow.identity.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ossflow.identity.auth.application.port.AccountRepositoryPort;
import com.ossflow.identity.auth.domain.Account;
import com.ossflow.identity.auth.domain.AccountProvider;
import com.ossflow.identity.auth.domain.AccountRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthIntegrationTest {

    @Autowired WebApplicationContext wac;
    @Autowired AccountRepositoryPort accountRepository;

    final ObjectMapper json = new ObjectMapper();

    MockMvc mvc() {
        return MockMvcBuilders.webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }

    @Test
    void register_returns_201() throws Exception {
        mvc().perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"test@example.com","password":"Test1234"}
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void login_with_verified_account_returns_access_token() throws Exception {
        // Arrange: save a verified account directly
        Account account = accountRepository.save(new Account(
                null, "verified@example.com",
                new BCryptPasswordEncoder(12).encode("Test1234"),
                AccountProvider.LOCAL, null, true, 0, AccountRole.ATHLETE, null, null
        ));
        assertThat(account.id()).isNotNull();

        // Act: login
        MvcResult result = mvc().perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"verified@example.com","password":"Test1234"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value("verified@example.com"))
                .andReturn();

        String accessToken = json.readTree(result.getResponse().getContentAsString())
                .get("accessToken").asText();
        assertThat(accessToken).isNotBlank();

        // Refresh cookie should be set
        assertThat(result.getResponse().getCookie("refresh_token")).isNotNull();
    }

    @Test
    void login_with_wrong_password_returns_422() throws Exception {
        accountRepository.save(new Account(
                null, "wrong@example.com",
                new BCryptPasswordEncoder(12).encode("RightPass1"),
                AccountProvider.LOCAL, null, true, 0, AccountRole.ATHLETE, null, null
        ));

        mvc().perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"wrong@example.com","password":"WrongPass1"}
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }

    @Test
    void refresh_with_valid_cookie_returns_new_access_token() throws Exception {
        // Arrange: create verified account and login to get refresh cookie
        accountRepository.save(new Account(
                null, "refresh@example.com",
                new BCryptPasswordEncoder(12).encode("Test1234"),
                AccountProvider.LOCAL, null, true, 0, AccountRole.ATHLETE, null, null
        ));

        MvcResult loginResult = mvc().perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"refresh@example.com","password":"Test1234"}
                                """))
                .andExpect(status().isOk())
                .andReturn();

        jakarta.servlet.http.Cookie refreshCookie = loginResult.getResponse().getCookie("refresh_token");
        assertThat(refreshCookie).isNotNull();

        // Act: refresh
        mvc().perform(post("/api/auth/refresh")
                        .cookie(refreshCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }
}
