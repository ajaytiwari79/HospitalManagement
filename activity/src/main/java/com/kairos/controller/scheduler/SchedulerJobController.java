package com.kairos.controller.scheduler;

import com.kairos.commons.service.mail.SendGridMailService;
import com.kairos.commons.service.scheduler.queue.JobQueueExecutor;
import com.kairos.dto.scheduler.queue.KairosSchedulerExecutorDTO;
import com.kairos.service.scheduler_service.ActivitySchedulerJobService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;

/**
 * @author pradeep
 * @date - 23/12/18
 */
@RestController()
@RequestMapping(API_V1+ SCHEDULER_EXECUTE_JOB)
public class SchedulerJobController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerJobController.class);

    @Inject
    private JobQueueExecutor schedulerToActivityQueueService;
    @Inject private MailService mailService;
    @Inject private ActivitySchedulerJobService activitySchedulerJobService;
    @Inject private SendGridMailService sendGridMailService;

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

    @ApiOperation(value = "Register job for night worker")
    @PostMapping(value = "/register_job_for_night_worker")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> registerJobForNightWorker() {
        activitySchedulerJobService.registerJobForNightWorker();
        return ResponseHandler.generateResponse(HttpStatus.OK, true,null);
    }

    @ApiOperation(value = "Update Phases in Ruletemplates")
    @GetMapping(value =  COUNTRY_URL+ "/register_job_for_wta_leave_count")
    public ResponseEntity<Map<String, Object>> regidterJobForWTALeaveCount(@PathVariable long countryId){
        activitySchedulerJobService.registerJobForWTALeaveCount(countryId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @ApiOperation(value = "Register job for protected days off")
    @PostMapping(value = "/register_job_for_protected_days_off")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> registerJobForProtectedDaysOff() {
        activitySchedulerJobService.registerJobForProtectedDaysOff();
        return ResponseHandler.generateResponse(HttpStatus.OK, true,null);
    }

    @ApiOperation(value = "create job for PayRoll Period ")
    @PutMapping(value="/payroll_period_job")
    public ResponseEntity<Map<String, Object>> createJobForPayrollPeriod() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activitySchedulerJobService.createJobForAddPayrollPeriod());

    }

    @ApiOperation(value = "create job of Planning Period ")
    @PutMapping(value="/planning_period_job")
    public ResponseEntity<Map<String, Object>> createJobForPlanningPeriod() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activitySchedulerJobService.createJobOfPlanningPeriod());

    }




}
