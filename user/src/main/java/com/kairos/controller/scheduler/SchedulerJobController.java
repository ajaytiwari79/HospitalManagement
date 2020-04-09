package com.kairos.controller.scheduler;

import com.kairos.commons.service.mail.SendGridMailService;
import com.kairos.commons.service.scheduler.queue.JobQueueExecutor;
import com.kairos.dto.scheduler.queue.KairosSchedulerExecutorDTO;
import com.kairos.service.scheduler.UserSchedulerJobService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_V1;
import static com.kairos.constants.ApiConstants.SCHEDULER_EXECUTE_JOB;

/**
 * @author pradeep
 * @date - 23/12/18
 */
@RestController
@RequestMapping(API_V1+ SCHEDULER_EXECUTE_JOB)
public class SchedulerJobController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerJobController.class);
    @Inject
    private JobQueueExecutor schedulerToActivityQueueService;
    @Inject private SendGridMailService sendGridMailService;
    @Inject
    private UserSchedulerJobService userSchedulerJobService;

    @ApiOperation("scheduler job execution")
    @PostMapping
    public ResponseEntity<Map<String,Object>> executeSchedulerJob(@RequestBody KairosSchedulerExecutorDTO job){
        try {
            schedulerToActivityQueueService.execute(job);
        }
        catch(Exception e) {
            LOGGER.error(e.getMessage(),e);
            sendGridMailService.sendMailToBackendOnException(e);

        }
        return ResponseHandler.generateResponse(HttpStatus.OK,true,true);
    }

    @ApiOperation(value = "Register job for seniority level")
    @PostMapping(value = "/register_job_for_seniority_level")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createJobForSeniorityLevel() {
        userSchedulerJobService.createJobForSeniorityLevel();
        return ResponseHandler.generateResponse(HttpStatus.OK, true,null);
    }

    @ApiOperation(value = "Register job for position end")
    @PostMapping(value = "/register_job_for_position_end")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createJobForPositionEnd() {
        userSchedulerJobService.createJobForPositionEnd();
        return ResponseHandler.generateResponse(HttpStatus.OK, true,null);
    }

    @ApiOperation(value = "Register job for add weather info of units")
    @PostMapping(value = "/register_job_for_add_weather_info_of_units")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createJobForAddTodayWeatherInfoOfUnits() {
        userSchedulerJobService.createJobForAddTodayWeatherInfoOfUnits();
        return ResponseHandler.generateResponse(HttpStatus.OK, true,null);
    }
}
