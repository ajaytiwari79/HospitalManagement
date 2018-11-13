package com.kairos.service.integration;


import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.enums.IntegrationOperation;
import com.kairos.persistence.model.organization_type.OrganizationTypeSubTypeAndServicesQueryResult;
import com.kairos.persistence.repository.organization.OrganizationTypeGraphRepository;
import com.kairos.rest_client.GdprServiceRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import javax.inject.Inject;

@Service
public class GdprIntegrationService {


    @Inject
    private GdprServiceRestClient genericRestClient;
    private Logger logger = LoggerFactory.getLogger(GdprIntegrationService.class);

    @Inject
    private OrganizationTypeGraphRepository organizationTypeGraphRepository;


    public boolean createDefaultDataForOrganization(Long countryId, Long unitId) {
        OrganizationTypeSubTypeAndServicesQueryResult organizationTypeSubType = organizationTypeGraphRepository.getOrganizationTypeSubTypesServiceAndSubServices(unitId);
        organizationTypeSubType.setCountryId(countryId);
        return genericRestClient.publish(organizationTypeSubType, unitId, true, IntegrationOperation.CREATE, "/inherit", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        });
    }


}
