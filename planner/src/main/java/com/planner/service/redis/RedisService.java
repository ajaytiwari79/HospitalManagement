package com.planner.service.redis;

import com.kairos.commons.config.EnvConfigCommon;
import com.kairos.commons.utils.CommonsExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.naming.AuthenticationException;
import java.util.Map;
import java.util.Optional;

import static com.kairos.constants.CommonConstants.LOCAL_PROFILE;


@Service
public class RedisService {


    private static Logger LOGGER = LoggerFactory.getLogger(RedisService.class);

    @Inject
    private RedisTemplate<String, Map<String, String>> valueOperations;
    @Inject private Environment envConfigCommon;

    public boolean verifyTokenInRedisServer(String userName, String accessToken) {
        if(!LOCAL_PROFILE.equals(envConfigCommon.getProperty("spring.profiles.active"))) {
            Map<String, String> userTokensFromDifferentMachine = valueOperations.opsForValue().get(userName);
            boolean validToken = false;
            if(userTokensFromDifferentMachine != null) {
                String userAccessToken = userTokensFromDifferentMachine.get(getTokenKey(accessToken));
                if(accessToken.equalsIgnoreCase(userAccessToken)) {
                    validToken = true;
                }
            }
            return validToken;
        }
        return true;
    }

    public boolean removeUserTokenFromRedisByUserNameAndToken(String userName,  String accessToken) {
        if(!LOCAL_PROFILE.equals(envConfigCommon.getProperty("spring.profiles.active"))) {
            boolean tokenRemoved = false;
            Map<String, String> userTokensFromDifferentMachine = valueOperations.opsForValue().get(userName);
            if(Optional.ofNullable(userTokensFromDifferentMachine).isPresent()) {
                String tokenKey = getTokenKey(accessToken);
                if(userTokensFromDifferentMachine.size() == 1) valueOperations.delete(userName);
                else {
                    if(!userTokensFromDifferentMachine.get(tokenKey).equalsIgnoreCase(accessToken)) {
                        throw new RuntimeException("invalid token");
                    }
                    userTokensFromDifferentMachine.remove(tokenKey);
                    valueOperations.opsForValue().set(userName, userTokensFromDifferentMachine);
                }
                tokenRemoved = true;
            } else {
                throw new RuntimeException("invalid token");
            }
            return tokenRemoved;
        }
        return true;
    }

    private String getTokenKey(String accessToken) {
        String[] tokenSplitString = accessToken.split("\\.");
        return tokenSplitString[tokenSplitString.length - 1].toLowerCase();
    }
}
