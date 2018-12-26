package com.kairos.controller.scheduler;

import com.kairos.commons.service.scheduler.queue.JobQueueExecutor;
import com.kairos.dto.scheduler.queue.KairosSchedulerExecutorDTO;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_V1;
import static com.kairos.constants.ApiConstants.SCHEDULER_EXECUTE_JOB;

/**
 * @author pradeep
 * @date - 23/12/18
 */
@RestController(API_V1+ SCHEDULER_EXECUTE_JOB)
public class SchedulerJobController {

    @Inject
    private JobQueueExecutor schedulerToActivityQueueService;

    @ApiOperation("scheduler job execution")
    @PostMapping()
    public ResponseEntity<Map<String,Object>> executeSchedulerJob(@RequestBody KairosSchedulerExecutorDTO job){
        schedulerToActivityQueueService.execute(job);
        return ResponseHandler.generateResponse(HttpStatus.OK,true,true);
    }


}
