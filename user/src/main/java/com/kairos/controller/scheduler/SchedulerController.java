package com.kairos.controller.scheduler;

import com.kairos.commons.service.scheduler.queue.JobQueueExecutor;
import com.kairos.dto.scheduler.queue.KairosSchedulerExecutorDTO;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;
import java.util.Map;

/**
 * @author pradeep
 * @date - 23/12/18
 */
@RequestMapping
public class SchedulerController {

    @Inject
    private JobQueueExecutor schedulerToActivityQueueService;

    @ApiOperation("scheduler job execution")
    @PostMapping()
    public ResponseEntity<Map<String,Object>> executeSchedulerJob(@RequestBody KairosSchedulerExecutorDTO job){
        schedulerToActivityQueueService.execute(job);
        return ResponseHandler.generateResponse(HttpStatus.OK,true,true);
    }
}
