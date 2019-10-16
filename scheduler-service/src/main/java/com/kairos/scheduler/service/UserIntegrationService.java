package com.kairos.scheduler.service;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.utils.UserRestClientAuth;
import com.kairos.dto.scheduler.queue.KairosSchedulerExecutorDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.scheduler.config.EnvConfig;
import com.kairos.scheduler.rest_client.UserRestClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;

@Service
public class UserIntegrationService {

    @Inject
    private UserRestClient userRestClient;
    @Inject
    private UserRestClientAuth userRestClientAuth;
    @Inject
    private EnvConfig envConfig;

    public String getTimeZoneOfUnit(Long unitId) {
        return userRestClient.publishRequest(null, unitId, true, IntegrationOperation.GET, "/time_zone", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<String>>() {
        },false);

    }

    public Map<Long,String> getTimeZoneOfAllUnits() {
        return userRestClient.publishRequest(null, null, false, IntegrationOperation.GET, "time_zone", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<Long,String>>>() {
        },true);

    }


    public void exceuteScheduleJob(KairosSchedulerExecutorDTO job){
        userRestClient.publishRequest(job,null,false, IntegrationOperation.CREATE,"scheduler_execute_job",null,new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        },true);
    }



}
