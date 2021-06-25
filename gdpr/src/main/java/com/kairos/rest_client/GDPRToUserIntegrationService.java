package com.kairos.rest_client;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.dto.user_context.CurrentUserDetails;
import com.kairos.enums.IntegrationOperation;
import com.kairos.service.exception.ExceptionService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstant.GET_ALL_UNITS_BY_COUNTRY;
import static com.kairos.constants.ApiConstant.GET_ORGANIZATION_IDS_BY_ORGANIZATION_SUB_TYPE_IDS_AND_SUB_SERVICE_IDS;

/**
 * Created By G.P.Ranjan on 10/9/19
 **/
@Service
public class GDPRToUserIntegrationService {

    @Inject
    private GDPRGenericRestClient gdprGenericRestClient;
    @Inject
    private ExceptionService exceptionService;

    public List<Long> getUnitIdsByOrgSubTypeId(Long countryId, List<Long> organizationSubTypeIds, List<Long> organizationSubServicesIds) {
        Map<String,List<Long>> requestBody = new HashMap<>();
        requestBody.put("organizationSubTypeIds",organizationSubTypeIds);
        requestBody.put("organizationSubServicesIds",organizationSubServicesIds);
        return gdprGenericRestClient.publishRequest(requestBody, countryId, false, IntegrationOperation.CREATE, GET_ORGANIZATION_IDS_BY_ORGANIZATION_SUB_TYPE_IDS_AND_SUB_SERVICE_IDS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Long>>>() {});
    }

    public List<Long> getAllUnitIdsByCountryId(Long countryId) {
        return gdprGenericRestClient.publishRequest(null, countryId, false, IntegrationOperation.GET, GET_ALL_UNITS_BY_COUNTRY, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Long>>>() {});
    }

    public CurrentUserDetails getCurrentUser(){
        return gdprGenericRestClient.publishRequest(null, null, true, IntegrationOperation.GET, "/get_current_user", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<CurrentUserDetails>>() {});
    }
}
