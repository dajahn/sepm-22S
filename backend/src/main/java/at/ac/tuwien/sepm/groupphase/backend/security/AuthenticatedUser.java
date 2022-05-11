package at.ac.tuwien.sepm.groupphase.backend.security;

import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class AuthenticatedUser implements UserDetails {

    private final User entity;
    private final UserDetails delegate;

    public AuthenticatedUser(User entity) {
        this.entity = entity;

        List<GrantedAuthority> grantedAuthorities;
        if (entity.getRole() == UserRole.ADMIN) {
            grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER");
        } else {
            grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_USER");
        }

        this.delegate = new org.springframework.security.core.userdetails.User(entity.getEmail(), entity.getPassword(), grantedAuthorities);
    }

    public User getEntity() {
        return entity;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.delegate.getAuthorities();
    }

    @Override
    public String getPassword() {
        return this.delegate.getPassword();
    }

    @Override
    public String getUsername() {
        return this.delegate.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.delegate.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.delegate.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.delegate.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return this.delegate.isEnabled();
    }
}
