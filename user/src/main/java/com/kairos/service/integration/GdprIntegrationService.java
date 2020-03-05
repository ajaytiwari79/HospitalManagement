package com.kairos.service.integration;


import com.kairos.persistence.model.organization_type.OrganizationTypeSubTypeAndServicesQueryResult;
import com.kairos.persistence.repository.organization.OrganizationTypeGraphRepository;
import com.kairos.rest_client.GdprServiceRestClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class GdprIntegrationService {


    @Inject
    private GdprServiceRestClient genericRestClient;
    private Logger logger = LoggerFactory.getLogger(GdprIntegrationService.class);

    @Inject
    private OrganizationTypeGraphRepository organizationTypeGraphRepository;


    public boolean createDefaultDataForOrganization(long countryId, long unitId) {
        OrganizationTypeSubTypeAndServicesQueryResult organizationTypeSubType = organizationTypeGraphRepository.getOrganizationTypeSubTypesServiceAndSubServices(unitId);
        organizationTypeSubType.setCountryId(countryId);
        return true;//genericRestClient.publishRequest(organizationTypeSubType, unitId, true, IntegrationOperation.CREATE, "/inherit", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() { });
    }


    public boolean createDefaultAssetForUnit(Long countryId,Long unitId, List<Long> orgSubTypeId, Long orgSubServiceId){
        BasicNameValuePair param = new BasicNameValuePair("countryId", countryId.toString());
        return true;//genericRestClient.publishRequest(orgSubTypeId, unitId, true, IntegrationOperation.CREATE, "/create_default_asset/org_sub_service/{orgSubService}", Collections.singletonList(param), new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {},orgSubServiceId);
    }
}
