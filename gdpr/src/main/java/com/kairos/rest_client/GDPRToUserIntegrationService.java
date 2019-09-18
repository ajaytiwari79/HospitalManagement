package com.kairos.rest_client;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.enums.IntegrationOperation;
import com.kairos.service.exception.ExceptionService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

import static com.kairos.constants.ApiConstant.*;

/**
 * Created By G.P.Ranjan on 10/9/19
 **/
@Service
public class GDPRToUserIntegrationService {

    @Inject
    private GDPRGenericRestClient gdprGenericRestClient;
    @Inject
    private ExceptionService exceptionService;

    public List<Long> getUnitIdsByOrgSubTypeId(Long countryId, List<Long> organizationSubTypeId) {
        return gdprGenericRestClient.publishRequest(organizationSubTypeId, countryId, false, IntegrationOperation.CREATE, GET_ORGANIZATION_IDS_ORGANIZATION_SUB_TYPE_Id, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Long>>>() {});
    }

    public List<Long> getAllUnitIdsByCountryId(Long countryId) {
        return gdprGenericRestClient.publishRequest(null, countryId, false, IntegrationOperation.GET, GET_ALL_UNITS_BY_COUNTRY, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Long>>>() {});
    }
}
