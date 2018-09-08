package com.kairos.gdpr.data_inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.gdpr.OrganizationSubType;
import com.kairos.gdpr.OrganizationType;
import com.kairos.gdpr.ServiceCategory;
import com.kairos.gdpr.SubServiceCategory;

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
