package com.kairos.dto.gdpr.data_inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.OrganizationSubType;
import com.kairos.dto.gdpr.OrganizationType;
import com.kairos.dto.gdpr.ServiceCategory;
import com.kairos.dto.gdpr.SubServiceCategory;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationMetaDataDTO {

    private OrganizationType organizationType;

    private OrganizationSubType organizationSubType;

    private ServiceCategory organizationService;

    private SubServiceCategory organizationSubService;

    public OrganizationType getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(OrganizationType organizationType) {
        this.organizationType = organizationType;
    }

    public OrganizationSubType getOrganizationSubType() {
        return organizationSubType;
    }

    public void setOrganizationSubType(OrganizationSubType organizationSubType) { this.organizationSubType = organizationSubType; }

    public ServiceCategory getOrganizationService() {
        return organizationService;
    }

    public void setOrganizationService(ServiceCategory organizationService) { this.organizationService = organizationService; }

    public SubServiceCategory getOrganizationSubService() {
        return organizationSubService;
    }

    public void setOrganizationSubService(SubServiceCategory organizationSubService) { this.organizationSubService = organizationSubService; }
}
