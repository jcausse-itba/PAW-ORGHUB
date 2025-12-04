package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.service.UserService;
import ar.edu.itba.paw.interfaces.service.exception.UserNotFoundException;
import ar.edu.itba.paw.model.enums.Role;
import ar.edu.itba.paw.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Component
@RequiredArgsConstructor
public class AuthUserDetailsService implements UserDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthUserDetailsService.class);

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        LOGGER.info("Login attempt for user {}.", email);

        User user;
        try {
            user = userService.findByEmail(email);
        } catch (UserNotFoundException e) {
            LOGGER.warn("User {} not found.", email);
            throw new UsernameNotFoundException(email);
        }

        LOGGER.debug("User {} found.", email);
        return new AuthUserDetails(user, List.of(new SimpleGrantedAuthority("ROLE_" + Role.USER.getRoleName())));
    }
}
