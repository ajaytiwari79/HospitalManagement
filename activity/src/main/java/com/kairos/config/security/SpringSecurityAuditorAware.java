package com.kairos.config.security;
/*
 *Created By Pavan on 2/2/19
 *
 */

import com.kairos.dto.activity.common.UserInfo;
import com.kairos.utils.user_context.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<UserInfo> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringSecurityAuditorAware.class);

    public Optional<UserInfo> getCurrentAuditor() {
        if (Optional.ofNullable(UserContext.getUserDetails()).isPresent() && Optional.ofNullable(UserContext.getUserDetails().getId()).isPresent()) {
            LOGGER.info("Created by or modified by " + UserContext.getUserDetails().getUserName());
            return Optional.of(new UserInfo(UserContext.getUserDetails().getId(),UserContext.getUserDetails().getEmail(),UserContext.getUserDetails().getFullName()));
        } else {
            return Optional.of(new UserInfo());
        }
    }
}
