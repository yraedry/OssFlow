package com.ossflow.testsupport;

import com.ossflow.identity.auth.domain.AccountRole;
import com.ossflow.identity.auth.infrastructure.security.AccountPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public final class TestSecurityContext {

    private TestSecurityContext() {}

    public static void setOwner(long ownerId) {
        AccountPrincipal principal = new AccountPrincipal(ownerId, "test-" + ownerId + "@example.com", AccountRole.ATHLETE);
        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    public static void setCoach(long ownerId) {
        AccountPrincipal principal = new AccountPrincipal(ownerId, "coach-" + ownerId + "@example.com", AccountRole.ATHLETE_COACH);
        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    public static void clear() {
        SecurityContextHolder.clearContext();
    }
}
