package com.kairos.config.security;
/*
 *Created By Pavan on 1/2/19
 *
 */

import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.utils.user_context.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<Long> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringSecurityAuditorAware.class);
    @Inject
    private UserGraphRepository userGraphRepository;

    public Optional<Long> getCurrentAuditor() {
        if (Optional.ofNullable(UserContext.getUserDetails()).isPresent() && Optional.ofNullable(UserContext.getUserDetails().getId()).isPresent()) {
            LOGGER.info("Created by or modified by " + UserContext.getUserDetails().getId());
            return Optional.of(UserContext.getUserDetails().getId());
        } else {
            return Optional.ofNullable(null);
        }
    }
}
