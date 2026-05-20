package com.ossflow.shared.web;

import com.ossflow.identity.auth.domain.AccountRole;
import com.ossflow.identity.auth.infrastructure.security.AccountPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CurrentOwnerTest {

    private final CurrentOwner currentOwner = new CurrentOwner();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void returns_principal_id_when_authenticated() {
        AccountPrincipal principal = new AccountPrincipal(42L, "user@example.com", AccountRole.ATHLETE);
        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThat(currentOwner.id()).isEqualTo(42L);
    }

    @Test
    void throws_when_no_authentication() {
        SecurityContextHolder.clearContext();
        assertThatThrownBy(currentOwner::id)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ownerId requerido");
    }

    @Test
    void throws_when_anonymous_authentication() {
        var anon = new AnonymousAuthenticationToken("k", "anonymous",
                List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
        SecurityContextHolder.getContext().setAuthentication(anon);

        assertThatThrownBy(currentOwner::id)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ownerId requerido");
    }
}
