package com.kairos.service.integration;


import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.enums.IntegrationOperation;
import com.kairos.persistence.model.organization_type.OrganizationTypeSubTypeAndServicesQueryResult;
import com.kairos.persistence.repository.organization.OrganizationTypeGraphRepository;
import com.kairos.rest_client.GdprServiceRestClient;
import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
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
        return genericRestClient.publishRequest(organizationTypeSubType, unitId, true, IntegrationOperation.CREATE, "/inherit", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        });
    }


    public boolean createDefaultAssetForUnit(long unitId, List<Long> orgSubTypeId, long OrgSubServiceId){
        return genericRestClient.publishRequest(orgSubTypeId, unitId, true, IntegrationOperation.CREATE, "/create_default_asset/org_sub_service/{orgSubService}", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {},OrgSubServiceId);
    }
}
