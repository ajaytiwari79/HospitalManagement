package com.kairos.config.security;

import com.kairos.service.redis.RedisService;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.persistence.model.auth.UserPrincipal;
import com.kairos.service.auth.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * @auther anil maurya
 */
public class CustomDefaultTokenServices extends DefaultTokenServices {
    private UserService userService;
    private RedisService redisService;
    private TokenStore tokenStore;

    public CustomDefaultTokenServices() {
        //default
    }

    public CustomDefaultTokenServices(UserService userService, RedisService redisService) {
        this.userService = userService;
        this.redisService = redisService;
    }

    private static final Logger log = LoggerFactory.getLogger(CustomDefaultTokenServices.class);


    @Transactional
    @Override
    public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
        log.info("adding user details information into oauth2 token");
        UserPrincipal user = (UserPrincipal) authentication.getUserAuthentication().getPrincipal();
        final Map<String, Object> userDetails = new HashMap<>();
        Map<String, Object> userDetailsMap = ObjectMapperUtils.copyPropertiesByMapper(user.getDetails(), Map.class);
        userDetailsMap.put("languageId", userService.getUserSelectedLanguageId(user.getUser().getId()));
        userDetails.put("details", userDetailsMap);
        authentication.setDetails(userDetails);
        OAuth2AccessToken accessToken = super.createAccessToken(authentication);
        //saveTokenInRedisServer(user, accessToken.toString());
        return accessToken;
    }

    private void saveTokenInRedisServer(UserPrincipal userPrincipal, String accessToken) {
        redisService.saveTokenInRedis(userPrincipal.getUsername(), accessToken);
    }


    public void setTokenStore(TokenStore tokenStore) {
        super.setTokenStore(tokenStore);
        this.tokenStore = tokenStore;
    }

    public TokenStore getTokenStore() {
        return tokenStore;
    }
}
