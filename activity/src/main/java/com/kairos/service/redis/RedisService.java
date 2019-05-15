package com.kairos.service.redis;

import com.kairos.commons.config.EnvConfigCommon;
import com.kairos.commons.utils.CommonsExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;

import static com.kairos.constants.CommonConstants.LOCAL;

/**
 * created by @bobby sharma
 */
@Service
public class RedisService extends CommonsExceptionUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisService.class);

    @Inject
    private RedisTemplate<String, Map<String, String>> valueOperations;

    @Inject
    private EnvConfigCommon envConfigCommon;

    public boolean verifyTokenInRedisServer(String userName, String accessToken) {
        boolean validToken = false;
        if(!envConfigCommon.getCurrentProfile().equals(LOCAL)) {
            Map<String, String> userTokens = valueOperations.opsForValue().get(userName);
            if(userTokens != null) {
                String userAccessToken = userTokens.get(getTokenKey(accessToken));
                if(accessToken.equalsIgnoreCase(userAccessToken)) {
                    validToken = true;
                }
            }
        } else {
            validToken = true;
        }
        return validToken;
    }

    public boolean removeUserTokenFromRedisByUserNameAndToken(String userName, String accessToken) {
        boolean tokenRemoved = false;
        if(!envConfigCommon.getCurrentProfile().equals(LOCAL)) {
            Map<String, String> userTokens = valueOperations.opsForValue().get(userName);
            if(Optional.ofNullable(userTokens).isPresent()) {
                String tokenKey = getTokenKey(accessToken);
                if(userTokens.size() == 1) valueOperations.delete(userName);
                else {
                    if(!userTokens.get(tokenKey).equalsIgnoreCase(accessToken)) {
                        internalServerError("message.redis.perssistedtoken.notEqualToRequestedToken");
                    }
                    userTokens.remove(tokenKey);
                    valueOperations.opsForValue().set(userName, userTokens);
                }
                tokenRemoved = true;
            } else {
                internalServerError("message.user.notFoundInRedis");
            }
        } else {
            tokenRemoved = true;
        }
        return tokenRemoved;
    }

    private String getTokenKey(String accessToken) {
        String[] tokenSplitString = accessToken.split("\\.");
        return tokenSplitString[tokenSplitString.length - 1].toLowerCase();
    }
}
