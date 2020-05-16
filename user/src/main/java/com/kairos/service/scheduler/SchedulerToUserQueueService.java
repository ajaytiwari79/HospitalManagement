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
        LOGGER.info("Job type----------------->{} with id {}",job.getJobSubType(),job.getId());
        switch(job.getJobSubType()) {
            case INTEGRATION:
                integrationJobService.runJob(job);
                break;
            case POSITION_END:
                positionService.endPositionProcess();
                break;
            case QUESTIONAIRE_NIGHTWORKER:
                break;
            case SENIORITY_LEVEL:
                employmentJobService.updateSeniorityLevelOnJobTrigger(job.getId(),job.getUnitId());
                break;
            case NIGHT_WORKER:
                employmentJobService.updateNightWorkers();
                break;
            case ADD_WEATHER_INFO:
                weatherService.saveWeatherInfoOfAllUnit();
                break;
            default:
                LOGGER.error("No execution route found for jobsubtype");
                break;
        }

    }
}
