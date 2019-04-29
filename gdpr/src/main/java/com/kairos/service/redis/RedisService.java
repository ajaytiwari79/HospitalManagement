package com.kairos.service.redis;

import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;

@Service
public class RedisService {

    private static Logger logger = LoggerFactory.getLogger(RedisService.class);

    @Inject
    private RedisTemplate<String, Map<String, String>> valueOperations;

    @Inject
    private ExceptionService exceptionService;


    public boolean checkIfUserExistInRedis(String userName) {
        return valueOperations.opsForValue().get(userName)!=null;

    }

    public boolean removeUserTokenFromRedisByClientIpAddress(String userName, String clientId) {
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
