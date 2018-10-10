package com.kairos.scheduler.service;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.utils.UserRestClientAuth;
import com.kairos.dto.user.auth.AccessTokenDTO;
import com.kairos.dto.user.organization.UnitTimeZoneMappingDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.scheduler.config.EnvConfig;
import com.kairos.scheduler.rest_client.UserRestClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
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
        String timezone = userRestClient.publishRequest(null, unitId, true, IntegrationOperation.GET, "/time_zone", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<String>>() {
        },false);

        return timezone;

    }

    public List<UnitTimeZoneMappingDTO> getTimeZoneOfAllUnits() {
        List<UnitTimeZoneMappingDTO> unitTimeZoneMappingDTOS = userRestClient.publishRequest(null, null, false, IntegrationOperation.GET, "time_zone", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<UnitTimeZoneMappingDTO>>>() {
        },true);

        return unitTimeZoneMappingDTOS;

    }




}
