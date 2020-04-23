package com.kairos.service.scheduler;


import com.kairos.commons.service.scheduler.queue.JobQueueExecutor;
import com.kairos.dto.scheduler.queue.KairosSchedulerExecutorDTO;
import com.kairos.service.employment.EmploymentJobService;
import com.kairos.service.staff.PositionService;
import com.kairos.service.weather.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class SchedulerToUserQueueService implements JobQueueExecutor {

    @Inject
    private PositionService positionService;
    @Inject
    private IntegrationJobsExecutorService integrationJobService;
    @Inject
    private EmploymentJobService employmentJobService;
    @Inject
    private WeatherService weatherService;

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerToUserQueueService.class);

    public void execute(KairosSchedulerExecutorDTO job) {
        LOGGER.info("Job type----------------->{}",job.getJobSubType());
        switch(job.getJobSubType()) {
            case INTEGRATION:
                LOGGER.info("Integration----------------->{}",job.getId());
                integrationJobService.runJob(job);
                break;
            case POSITION_END:
                LOGGER.info("End Position----------------->{}",job.getId());
                positionService.endPositionProcess();
                break;
            case QUESTIONAIRE_NIGHTWORKER:
                LOGGER.info("Questionaire nightworker----------------->{}",job.getId());
                break;
            case SENIORITY_LEVEL:
                LOGGER.info("Update Seniority Level----------------->{}",job.getId());
                employmentJobService.updateSeniorityLevelOnJobTrigger(job.getId(),job.getUnitId());
                break;
            case NIGHT_WORKER:
                LOGGER.info("Update Night Workers----------------->{}",job.getId());
                employmentJobService.updateNightWorkers();
                break;
            case ADD_WEATHER_INFO:
                LOGGER.info("Save Weather Info Of All Unit----------------->{}",job.getId());
                weatherService.saveWeatherInfoOfAllUnit();
                break;
            default:
                LOGGER.error("No execution route found for jobsubtype");
                break;
        }

    }
}
