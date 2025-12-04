package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.model.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public final class AuthUserDetails extends org.springframework.security.core.userdetails.User {
    @Getter
    private final User domainUser;

    public AuthUserDetails(User domainUser, Collection<? extends GrantedAuthority> authorities) {
        super(domainUser.getUserEmail(), domainUser.getEncodedPassword(), authorities);
        this.domainUser = domainUser;
    }
}
