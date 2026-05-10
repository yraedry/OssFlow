package com.ossflow.shared.web;

import com.ossflow.identity.auth.infrastructure.security.AccountPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentOwner {
    public Long id() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AccountPrincipal principal)) {
            return 1L; // fallback for tests and dev without auth
        }
        return principal.id();
    }
}
