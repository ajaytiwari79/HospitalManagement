package com.kairos.dto.data_inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.OrganizationSubTypeDTO;
import com.kairos.dto.OrganizationTypeDTO;
import com.kairos.dto.ServiceCategoryDTO;
import com.kairos.dto.SubServiceCategoryDTO;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationMetaDataDTO {

    private OrganizationTypeDTO organizationType;

    private OrganizationSubTypeDTO organizationSubType;

    private ServiceCategoryDTO organizationService;

    private SubServiceCategoryDTO organizationSubService;

    public OrganizationTypeDTO getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(OrganizationTypeDTO organizationType) {
        this.organizationType = organizationType;
    }

    public OrganizationSubTypeDTO getOrganizationSubType() {
        return organizationSubType;
    }

    public void setOrganizationSubType(OrganizationSubTypeDTO organizationSubType) { this.organizationSubType = organizationSubType; }

    public ServiceCategoryDTO getOrganizationService() {
        return organizationService;
    }

    public void setOrganizationService(ServiceCategoryDTO organizationService) { this.organizationService = organizationService; }

    public SubServiceCategoryDTO getOrganizationSubService() {
        return organizationSubService;
    }

    public void setOrganizationSubService(SubServiceCategoryDTO organizationSubService) { this.organizationSubService = organizationSubService; }
}
