package com.kairos.service.integration;


import com.kairos.dto.gdpr.OrganizationSubType;
import com.kairos.dto.gdpr.ServiceCategory;
import com.kairos.dto.gdpr.SubServiceCategory;
import com.kairos.dto.gdpr.data_inventory.OrganizationTypeAndSubTypeIdDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.persistence.model.organization_type.OrganizationTypeSubTypeAndServicesQueryResult;
import com.kairos.persistence.repository.organization.OrganizationTypeGraphRepository;
import com.kairos.rest_client.GdprServiceRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
@Transactional
public class GdprIntegrationService {


    @Inject
    GdprServiceRestClient genericRestClient;
    private Logger logger = LoggerFactory.getLogger(GdprIntegrationService.class);

    @Inject
    private OrganizationTypeGraphRepository organizationTypeGraphRepository;


    public void createDefaultDataForOrganization(Long countryId, Long unitId) {
        OrganizationTypeSubTypeAndServicesQueryResult organizationTypeSubType = organizationTypeGraphRepository.getOrganizationTypeSubTypesServiceAndSubServices(unitId);
        OrganizationTypeAndSubTypeIdDTO organizationTypeAndSubTypeIdDTO = new OrganizationTypeAndSubTypeIdDTO(Collections.singletonList(organizationTypeSubType.getId()),
                organizationTypeSubType.getOrganizationSubTypes().stream().map(OrganizationSubType::getId).collect(Collectors.toList()),
                organizationTypeSubType.getOrganizationServices().stream().map(ServiceCategory::getId).collect(Collectors.toList()),
                organizationTypeSubType.getOrganizationSubServices().stream().map(SubServiceCategory::getId).collect(Collectors.toList()));
        organizationTypeAndSubTypeIdDTO.setCountryId(countryId);
        genericRestClient.publish(organizationTypeAndSubTypeIdDTO, unitId, true, IntegrationOperation.CREATE, "/inherit", null);
    }


}
