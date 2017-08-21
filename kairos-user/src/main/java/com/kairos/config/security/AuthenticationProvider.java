package com.kairos.config.security;
import com.kairos.persistence.model.user.auth.Role;
import com.kairos.persistence.model.user.auth.User;
import com.kairos.persistence.repository.user.auth.UserRoleGraphRepository;
import com.kairos.service.auth.UserDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 *  Provide Authentication Service to Application.
 1. Authenticate User and fetch Roles using UserDetailService.
 */
@Component
public class AuthenticationProvider implements org.springframework.security.authentication.AuthenticationProvider {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationProvider.class);

    @Inject
    UserDetailService userDetailService;
    @Inject
    UserRoleGraphRepository userRoleGraphRepository;

    /**
     * Calls userDetailService with username and password and return user if exists.
     * if user is !null ,calls userRoleGraphRepository and return Roles
     * @param authentication
     * @return Authentication
     * @throws AuthenticationException
     */
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        List<GrantedAuthority> grantedAuths = new ArrayList<>();
        User user = userDetailService.loadUserByUsername(username, password);

        logger.info("Authenticated organization_service.manage information"+user);
        if (user != null) {
            logger.info("fetching roles for :"+user.getFirstName()+"with ID: "+user.getId());
            for(Role role: userRoleGraphRepository.findAllByUser(user.getId())) {
                grantedAuths.add(new SimpleGrantedAuthority(role.getAuthority()));
                logger.info("Roles are: "+role.getAuthority());
            }
            return new UsernamePasswordAuthenticationToken(username, password,
                    grantedAuths);
        }
        return null;
    }




    /**
     * Checks if this class suppoorts authentication and return boolean.
     * @param authentication
     * @return boolean
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}