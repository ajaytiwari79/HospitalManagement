package com.kairos.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.persistence.model.user.auth.UserPrincipal;
import com.kairos.util.userContext.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.HashMap;
import java.util.Map;

public class CustomJwtAccessTokenConverter extends JwtAccessTokenConverter {
    private static final Logger log = LoggerFactory.getLogger(CustomJwtAccessTokenConverter.class);

    private static final String USER_DETAILS_KEY = "details";
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        log.debug("adding additional information {}",authentication);

        UserPrincipal user=(UserPrincipal)authentication.getUserAuthentication().getPrincipal();
        Map<String, Object> additionalInfo = new HashMap<>();
       // additionalInfo.put("details",user.getDetails());
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return super.enhance(accessToken, authentication);
    }

    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
        log.debug("extractAuthentication additional information {} from token");
        final OAuth2Authentication authentication =    super.extractAuthentication(map);
         Map<String, Object> additionalInfo = new HashMap<>();
          ObjectMapper mapper=new ObjectMapper();
         CurrentUserDetails details=mapper.convertValue(map.get(USER_DETAILS_KEY),CurrentUserDetails.class);
         authentication.setDetails(details);
         UserContext.setUserDetails(details);
         return authentication;
    }
}
