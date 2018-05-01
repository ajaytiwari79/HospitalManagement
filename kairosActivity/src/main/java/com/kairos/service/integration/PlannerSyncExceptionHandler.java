package com.kairos.service.integration;

import com.kairos.activity.service.organization.OrganizationActivityService;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class PlannerSyncExceptionHandler {
    {
        int i=0;
    }
    private final Logger logger = LoggerFactory.getLogger(OrganizationActivityService.class);
        @AfterThrowing(pointcut = "execution(* com.kairos.activity.service.integration.*.*(..))", throwing = "ex")
        public void logError(Exception ex) {
            logger.error("Exception while syncing to planner.",ex);
    }
}
