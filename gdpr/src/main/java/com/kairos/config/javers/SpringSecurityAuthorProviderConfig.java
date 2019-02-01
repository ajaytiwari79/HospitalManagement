package com.kairos.config.javers;

import com.kairos.utils.user_context.UserContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SpringSecurityAuthorProviderConfig /*implements AuthorProvider*/ {


    //@Override
    public String provide() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return "unauthenticated";
        }
        String userName = UserContext.getUserDetails().getFirstName() + " " + UserContext.getUserDetails().getLastName();
        return auth.getName() + "-" + userName ;
    }


}
