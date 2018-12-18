package com.kairos.service.auth;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 *  UserDetailService
 * 1.Fetch User from DB
 * 2.Decrypt password and Authenticate User.
 */
@Transactional
@Service
public class UserDetailService {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private UserGraphRepository userRepository;

    @Inject
    @Lazy
    private PasswordEncoder passwordEncoder;

    /**
     * Calls UserGraphRepository and find user by provided Username & password &
     * return User
     * @param userName
     * @param password
     * @return
     * @throws UsernameNotFoundException
     */
    public User loadUserByEmail(String userName , String password) throws UsernameNotFoundException {
        User user=  userRepository.findUserByEmailInAnyOrganization("(?i)"+userName);
         if (user == null) {
              logger.info("User is null");
               return null;
            } else {
                if (passwordEncoder.matches(password, user.getPassword())){
                    logger.info("password matched");
                    return user;
                }
             logger.info("password  not  matched");
                return null;
//                return passwordEncoder.matches(password, user.getPassword()) ? user : null;
            }
        }

    }


