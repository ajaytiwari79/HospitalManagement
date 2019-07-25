package com.kairos.config.javers;

import com.kairos.utils.user_context.UserContext;
import org.javers.spring.auditable.AuthorProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

class SpringSecurityAuthorProviderConfig implements AuthorProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringSecurityAuthorProviderConfig.class);

    @Override
    public String provide() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = null;
        if (auth == null) {
            return "unauthenticated";
        }else if (UserContext.getUserDetails() != null) {
            userName = UserContext.getUserDetails().getFirstName() + " " + UserContext.getUserDetails().getLastName();
            return auth.getName() + "-" + userName;
        }else {
            return auth.getName();
        }
    }


}
