package com.kairos.service.auth;

import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class RedisService {

    private static Logger logger = LoggerFactory.getLogger(RedisService.class);

    @Inject
    private RedisTemplate<String, Map<String, String>> valueOperations;

    @Inject
    private ExceptionService exceptionService;

    public void saveTokenInRedis(String userName, String token, String clientId) {

        Map<String, String> userTokensFromDifferentMachine = valueOperations.opsForValue().get(userName);
        if (Optional.ofNullable(userTokensFromDifferentMachine).isPresent()) {
            userTokensFromDifferentMachine.put(clientId, token);
        } else {
            userTokensFromDifferentMachine = new HashMap<>();
            userTokensFromDifferentMachine.put(clientId, token);
        }
        valueOperations.opsForValue().set(userName, userTokensFromDifferentMachine);
        logger.info("saved user token into redis");

    }

    public void invalidateAllTokenOfUser(String userName) {
        boolean tokenDeleted = valueOperations.delete(userName);
        if (!tokenDeleted) {
            exceptionService.internalServerError("Unable to delete all tokens for user " + userName);
        }

    }

    public boolean expireTokenOfUserWhileLogout(String userName, String clientId) {
        boolean tokenRemoved = false;
        Map<String, String> userTokensFromDifferentMachine = valueOperations.opsForValue().get(userName);
        if (Optional.ofNullable(userTokensFromDifferentMachine).isPresent()) {
            userTokensFromDifferentMachine.remove(clientId);
            tokenRemoved = true;
        } else {
            exceptionService.internalServerError("user token not found ");
        }
        return tokenRemoved;
    }

}
