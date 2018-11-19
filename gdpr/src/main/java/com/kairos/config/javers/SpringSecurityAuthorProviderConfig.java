package com.kairos.config.javers;

import com.kairos.utils.user_context.UserContext;
import org.javers.spring.auditable.AuthorProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SpringSecurityAuthorProviderConfig implements AuthorProvider {


    @Override
    public String provide() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = UserContext.getUserDetails().getFirstName() + " " + UserContext.getUserDetails().getLastName();

        if (auth == null) {
            return "unauthenticated";
        }

        return auth.getName() + "(" + userName + ")";
    }


}
