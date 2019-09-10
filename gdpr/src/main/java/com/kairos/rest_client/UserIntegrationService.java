package com.kairos.rest_client;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.enums.IntegrationOperation;
import com.kairos.service.exception.ExceptionService;
import lombok.NoArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

import static com.kairos.constants.ApiConstant.GET_ORGANIZATION_IDS_ORGANIZATION_SUB_TYPE_Id;

/**
 * Created By G.P.Ranjan on 10/9/19
 **/
@Service
@Transactional
@NoArgsConstructor
public class UserIntegrationService {
    @Inject
    private GenericRestClient genericRestClient;
    @Inject
    private ExceptionService exceptionService;

    public List<Long> getUnitIdsByOrgSubTypeId(Long countryId, List<Long> organizationSubTypeId) {
        return genericRestClient.publishRequest(organizationSubTypeId, countryId, false, IntegrationOperation.GET, GET_ORGANIZATION_IDS_ORGANIZATION_SUB_TYPE_Id, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Long>>>() {});
    }
}
