package com.kairos.service.integration;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class PlannerSyncExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(PlannerSyncExceptionHandler.class);
        @AfterThrowing(pointcut = "execution(* com.kairos.service.integration.*.*(..))", throwing = "ex")
        public void logError(Exception ex) {
            logger.error("Exception while syncing to planner.",ex);
    }
}
