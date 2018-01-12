package com.kairos.config.aop;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by neuron on 29/11/16.
 */
@Aspect
@Component
public class NeoLoggingAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(NeoLoggingAspect.class);

   /* @Inject
    private AppLogService appLogService;*/

    @After("execution(* org.springframework.data.repository.CrudRepository.save*(..))")
    public void neo4jSaveOperation(JoinPoint joinPoint){
        for(Object entity: joinPoint.getArgs()){
          //  appLogService.persistLog(entity.toString());
        }
       // LOGGER.info("Method Hijacked "+joinPoint.getSignature().getName());
    }





}
