package com.ossflow.shared.web;

import com.ossflow.identity.auth.domain.AccountRole;
import com.ossflow.identity.auth.infrastructure.security.AccountPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentOwner {
    public Long id() {
        return principal().id();
    }

    public AccountRole role() {
        return principal().role();
    }

    private AccountPrincipal principal() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AccountPrincipal principal)) {
            throw new IllegalStateException("ownerId requerido: ningún AccountPrincipal en el contexto de seguridad");
        }
        return principal;
    }
}
