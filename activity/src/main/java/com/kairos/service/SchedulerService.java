package com.kairos.service;

import com.kairos.service.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;

import static com.kairos.commons.utils.ObjectUtils.newHashSet;

@Service
public class SchedulerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerService.class);

    @Inject
    private RedisService redisService;

    @Scheduled(cron="0 0 0 * * ?")
    public void dailyJob(){
        LOGGER.info("Daily Job Execution {}",new Date());
        redisService.removeKeyFromCacheAsyscronously(newHashSet("getAccumulatedTimebankAndDelta*"));
    }
}
