package com.kairos.config.security;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.auth.UserPrincipal;
import com.kairos.service.auth.UserService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.constants.UserMessagesConstants.INTERNAL_SERVER_ERROR;

/**
 * @auther anil maurya
 */
public class CustomDefaultTokenServices extends DefaultTokenServices {
    public static final String BEARER = "bearer ";
    private UserService userService;
    private RedisService redisService;
    private TokenStore tokenStore;
    private ExceptionService exceptionService;

    public CustomDefaultTokenServices(UserService userService, RedisService redisService, TokenStore tokenStore, ExceptionService exceptionService) {
        this.userService = userService;
        this.redisService = redisService;
        this.setTokenStore(tokenStore);
        this.exceptionService = exceptionService;
    }

    private static final Logger log = LoggerFactory.getLogger(CustomDefaultTokenServices.class);


    @Transactional
    @Override
    public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
        log.info("adding user details information into oauth2 token");
        UserPrincipal user = (UserPrincipal) authentication.getUserAuthentication().getPrincipal();
        OAuth2AccessToken accessToken = getoAuth2AccessToken(authentication, user);
        return accessToken;
    }

    private OAuth2AccessToken getoAuth2AccessToken(OAuth2Authentication authentication, UserPrincipal user) {
        final Map<String, Object> userDetails = new HashMap<>();
        Map<String, Object> userDetailsMap = ObjectMapperUtils.copyPropertiesByMapper(user.getDetails(), Map.class);
        userDetailsMap.put("languageId", userService.getUserSelectedLanguageId(user.getUser().getId()));
        userDetails.put("details", userDetailsMap);
        authentication.setDetails(userDetails);
        OAuth2AccessToken accessToken = super.createAccessToken(authentication);
        saveTokenInRedisServer(user, accessToken.toString());
        return accessToken;
    }

    public String updateToken(String token, User user){
        OAuth2Authentication oAuth2Authentication = super.loadAuthentication(token.replace(BEARER,""));
        OAuth2AccessToken oAuth2AccessToken = this.getoAuth2AccessToken(oAuth2Authentication,new UserPrincipal(user, new ArrayList<>()));
        if(isNull(oAuth2AccessToken)){
            exceptionService.invalidRequestException(INTERNAL_SERVER_ERROR);
        }
        return BEARER +oAuth2AccessToken.toString();
    }

    private void saveTokenInRedisServer(UserPrincipal userPrincipal, String accessToken) {
        redisService.saveTokenInRedis(userPrincipal.getUsername(), accessToken);
    }


    @Override
    public void setTokenStore(TokenStore tokenStore) {
        super.setTokenStore(tokenStore);
        this.tokenStore = tokenStore;
    }

    public TokenStore getTokenStore() {
        return tokenStore;
    }
}
