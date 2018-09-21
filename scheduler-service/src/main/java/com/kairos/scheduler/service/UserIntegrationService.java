package com.kairos.scheduler.service;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.enums.IntegrationOperation;
import com.kairos.scheduler.rest_client.UserRestClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class UserIntegrationService {

    @Inject
    private UserRestClient userRestClient;


    public String getTimeZoneOfUnit(Long unitId) {
        String timezone = userRestClient.publishRequest(null, unitId, true, IntegrationOperation.GET, "/time_zone", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<String>>() {
        });

        return timezone;

    }

}
