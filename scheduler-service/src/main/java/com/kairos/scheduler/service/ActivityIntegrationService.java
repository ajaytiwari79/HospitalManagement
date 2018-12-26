package com.kairos.scheduler.service;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.dto.scheduler.queue.KairosSchedulerExecutorDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.scheduler.rest_client.ActivityRestClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

import static com.kairos.scheduler.constants.ApiConstants.SCHEDULER_EXECUTE_JOB;

/**
 * @author pradeep
 * @date - 23/12/18
 */

@Service
public class ActivityIntegrationService {

    @Inject private
    ActivityRestClient activityRestClient;


    public void exceuteScheduleJob(KairosSchedulerExecutorDTO job){
        activityRestClient.publishRequest(job,null,false, IntegrationOperation.CREATE,SCHEDULER_EXECUTE_JOB,null,new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        },true);
    }

}
