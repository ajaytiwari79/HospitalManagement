package com.kairos.service.redis;

import com.kairos.service.exception.ExceptionService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;

@Service
public class RedisService {


    @Inject
    private RedisTemplate<String, Map<String, String>> valueOperations;

    @Inject
    private ExceptionService exceptionService;


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

    public boolean removeUserTokenFromRedisByClientIpAddress(String userName,  String accessToken) {
        boolean tokenRemoved = false;
        Map<String, String> userTokensFromDifferentMachine = valueOperations.opsForValue().get(userName);
        if (Optional.ofNullable(userTokensFromDifferentMachine).isPresent()) {
            String tokenKey=getTokenKey(accessToken);
            if (Integer.valueOf(userTokensFromDifferentMachine.size()).equals(1))
                valueOperations.delete(userName);
            else {
                if (!userTokensFromDifferentMachine.get(tokenKey).equalsIgnoreCase(accessToken)) {
                    exceptionService.internalServerError("message.redis.perssistedtoken.notEqualToRequestedToken");
                }
                userTokensFromDifferentMachine.remove(accessToken);
                valueOperations.opsForValue().set(userName, userTokensFromDifferentMachine);
            }
            tokenRemoved = true;
        } else {
            exceptionService.internalServerError("message.user.notFoundInRedis");
        }
        return tokenRemoved;
    }

    private String getTokenKey(String accessToken) {
        String[] tokenSplitString = accessToken.split("\\.");
        return tokenSplitString[tokenSplitString.length - 1].toLowerCase();
    }
}
