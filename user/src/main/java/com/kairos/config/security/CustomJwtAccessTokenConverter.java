package com.kairos.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.persistence.model.auth.UserPrincipal;
import com.kairos.util.user_context.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.Map;

public class CustomJwtAccessTokenConverter extends JwtAccessTokenConverter {
    private static final Logger log = LoggerFactory.getLogger(CustomDefaultTokenServices.class);
    private static final String USER_DETAILS_KEY = "details";

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        UserPrincipal user=(UserPrincipal)authentication.getUserAuthentication().getPrincipal();
        final Map<String, Object> authDetails = (Map<String, Object>)authentication.getDetails();

         ((DefaultOAuth2AccessToken)accessToken).setAdditionalInformation(authDetails);
        return super.enhance(accessToken, authentication);
    }

    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
        log.info("extracting authencation from token {}",map);
        final OAuth2Authentication authentication =super.extractAuthentication(map);
        ObjectMapper mapper=new ObjectMapper();
        CurrentUserDetails details=mapper.convertValue(map.get(USER_DETAILS_KEY),CurrentUserDetails.class);
        log.info("setting user details into authencation {}",details);
        authentication.setDetails(details);
        UserContext.setUserDetails(details);

        return authentication;
    }
}
