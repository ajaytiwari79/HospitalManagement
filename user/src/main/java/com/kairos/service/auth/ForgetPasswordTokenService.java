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
public class ForgetPasswordTokenService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    UserService userService;
    @Inject
    UserGraphRepository userGraphRepository;


    public String createForgotPasswordToken(User user) {
        String token;
        token = UUID.randomUUID().toString().toUpperCase();
        user.setForgotTokenRequestTime(DateUtils.getLocalDateTime());
        user.setForgotPasswordToken(token);
        userGraphRepository.save(user);
        return token;
    }


}
