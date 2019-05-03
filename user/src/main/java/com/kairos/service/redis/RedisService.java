package com.kairos.service.redis;

import com.kairos.commons.utils.CommonsExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * created by @bobby sharma
 */
@Service
public class RedisService extends CommonsExceptionUtil {


    private static Logger LOGGER = LoggerFactory.getLogger(RedisService.class);

    @Inject
    private RedisTemplate<String, Map<String, String>> valueOperations;

    public void saveTokenInRedis(String userName, String accessToken) {

        Map<String, String> userTokensFromDifferentMachine = valueOperations.opsForValue().get(userName);
        String tokenKey = getTokenKey(accessToken);
        if (Optional.ofNullable(userTokensFromDifferentMachine).isPresent()) {
            userTokensFromDifferentMachine.put(tokenKey, accessToken);
        } else {
            userTokensFromDifferentMachine = new HashMap<>();
            userTokensFromDifferentMachine.put(tokenKey, accessToken);
        }
        valueOperations.opsForValue().set(userName, userTokensFromDifferentMachine);
        LOGGER.info("saved user token into redis");

    }


    public void invalidateAllTokenOfUser(String userName) {
        valueOperations.delete(userName);
    }



    public boolean verifyTokenInRedisServer(String userName, String accessToken) {
        Map<String, String> userTokensFromDifferentMachine = valueOperations.opsForValue().get(userName);
        boolean validToken = false;
        if (userTokensFromDifferentMachine != null) {
            String userAccessToken = userTokensFromDifferentMachine.get(getTokenKey(accessToken));
            if (accessToken.equalsIgnoreCase(userAccessToken)) {
                validToken = true;
            }
        }
        return validToken;
    }

    public boolean removeUserTokenFromRedisByUserNameAndToken(String userName,  String accessToken) {
        boolean tokenRemoved = false;
        Map<String, String> userTokensFromDifferentMachine = valueOperations.opsForValue().get(userName);
        if (Optional.ofNullable(userTokensFromDifferentMachine).isPresent()) {
            String tokenKey=getTokenKey(accessToken);
            if (userTokensFromDifferentMachine.size()==1)
                valueOperations.delete(userName);
            else {
                if (!userTokensFromDifferentMachine.get(tokenKey).equalsIgnoreCase(accessToken)) {
                    internalServerError("message.redis.perssistedtoken.notEqualToRequestedToken");
                }
                userTokensFromDifferentMachine.remove(tokenKey);
                valueOperations.opsForValue().set(userName, userTokensFromDifferentMachine);
            }
            tokenRemoved = true;
        } else {
            internalServerError("message.user.notFoundInRedis");
        }
        return tokenRemoved;
    }

    private String getTokenKey(String accessToken) {
        String[] tokenSplitString = accessToken.split("\\.");
        return tokenSplitString[tokenSplitString.length - 1].toLowerCase();
    }
}
