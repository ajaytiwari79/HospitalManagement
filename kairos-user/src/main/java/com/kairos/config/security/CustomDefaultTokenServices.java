package com.kairos.config.security;

import com.kairos.persistence.model.user.auth.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * @auther anil maurya
 *
 *
 */

public class CustomDefaultTokenServices extends DefaultTokenServices {
    private static final Logger log = LoggerFactory.getLogger(CustomDefaultTokenServices.class);
    @Transactional
    @Override
    public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
        log.info("adding user details information into oauth2 token");
        UserPrincipal user=(UserPrincipal)authentication.getUserAuthentication().getPrincipal();
        final Map<String, Object> userDetails = new HashMap<>();
         userDetails.put("details", user.getDetails());
         authentication.setDetails(userDetails);
        return super.createAccessToken(authentication);

    }

}
