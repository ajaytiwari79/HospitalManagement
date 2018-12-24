package com.kairos.scheduler.controller;

import com.kairos.dto.scheduler.queue.KairosSchedulerLogsDTO;
import com.kairos.scheduler.service.scheduler_panel.SchedulerPanelService;
import com.kairos.scheduler.utils.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;

import static com.kairos.scheduler.constants.ApiConstants.API_V1;
import static com.kairos.scheduler.constants.ApiConstants.JOB_DETAILS;

/**
 * @author pradeep
 * @date - 24/12/18
 */
@RequestMapping(API_V1+JOB_DETAILS)
public class JobDetailsController {

    @Inject
    private SchedulerPanelService schedulerPanelService;


    @PostMapping
    @ApiOperation("create job details")
    public ResponseEntity<Map<String, Object>> createJobDetails(@RequestBody KairosSchedulerLogsDTO schedulerLogs) throws IOException {
        schedulerPanelService.createJobScheduleDetails(schedulerLogs);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);

    }

}
