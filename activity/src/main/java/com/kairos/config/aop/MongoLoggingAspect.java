package com.kairos.config.aop;

import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by neuron on 29/11/16.
 */
@Aspect
@Component
public class MongoLoggingAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoLoggingAspect.class);

   /* @Inject
    private AppLogService appLogService;

    @After("execution(* com.kairos.activity.service.MongoBaseService.save*(..))")
    public void mongoSaveOperation(JoinPoint joinPoint){
        for(Object entity: joinPoint.getArgs()){
            appLogService.persistLog(entity.toString());
        }
       // LOGGER.info("Method Hijacked "+joinPoint.getSignature().getName());
    }*/



}
