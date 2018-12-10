package com.kairos.service.auth;
import com.kairos.commons.utils.DateUtils;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.UUID;

/**
 *  Calls UserGraphRepository to perform CRUD operation on  token
 */
@Transactional
@Service
public class TokenService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    UserService userService;
    @Inject
    UserGraphRepository userGraphRepository;

    /**
     * Calls UserGraphRepository and fetch token , if token is null generate new token,
     * and return
     * @param user
     * @return String token
     */
    public String createToken(User user) {
        String token;
        // Check if Token exists

            // generate token
        token = UUID.randomUUID().toString().toUpperCase();
        user.setAccessToken(token);
        userGraphRepository.save(user);
        return token;
    }

    public String createForgotPasswordToken(User user) {
        String token;
        token = UUID.randomUUID().toString().toUpperCase();
        user.setForgotTokenRequestTime(DateUtils.getLocalDateTime());
        user.setForgotPasswordToken(token);
        userGraphRepository.save(user);
        return token;
    }



    /**
     * Calls UserGraphRepository and find token ,
     * return true if exists.
     * @param token
     * @return boolean
     */
    public boolean validateToken(String token) {
        return userService.findByAccessToken(token) != null;
    }



    /**
     * This is called when user hits logout.
     * Calls UserGraphRepository find the token and remove it.
     * @param accessToken
     * @return boolean
     */
    public boolean removeToken(String accessToken) {
        User currentUser = userService.findAndRemoveAccessToken(accessToken);
        currentUser.setAccessToken(null);
        userGraphRepository.save(currentUser);
        return currentUser.getAccessToken() == null;
    }


    /**
     * Find the user by token and returns user
     * @param token
     * @return User
     */
    public User getUserByToken(String token){
        return userService.findByAccessToken(token);
    }


}
