package com.ossflow.identity.auth.infrastructure.security;

import com.ossflow.identity.auth.domain.AccountRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record AccountPrincipal(Long id, String email, AccountRole role) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == AccountRole.ATHLETE_COACH) {
            return List.of(
                new SimpleGrantedAuthority("ROLE_ATHLETE"),
                new SimpleGrantedAuthority("ROLE_COACH")
            );
        }
        return List.of(new SimpleGrantedAuthority("ROLE_ATHLETE"));
    }

    @Override public String getPassword() { return null; }
    @Override public String getUsername() { return email; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
