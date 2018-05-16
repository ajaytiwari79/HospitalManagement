package com.kairos.persistance.model.asset.dto;

import com.kairos.persistance.model.dto.CustomOrganizationTypeAndServiceDto;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import java.util.List;

public class MasterAssetDto {

    @NotNullOrEmpty(message = "error.message.name.cannotbe.null.or.empty")
    private  String name;

    @NotNullOrEmpty(message = "error.message.name.cannotbe.null.or.empty")
    private String description;
    private List<Long> organisationType;

    private List <Long> organisationSubType;
    private List <Long>organisationService;
    private List <Long> organisationSubService;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(List<Long> organisationType) {
        this.organisationType = organisationType;
    }

    public List<Long> getOrganisationSubType() {
        return organisationSubType;
    }

    public void setOrganisationSubType(List<Long> organisationSubType) {
        this.organisationSubType = organisationSubType;
    }

    public List<Long> getOrganisationService() {
        return organisationService;
    }

    public void setOrganisationService(List<Long> organisationService) {
        this.organisationService = organisationService;
    }

    public List<Long> getOrganisationSubService() {
        return organisationSubService;
    }

    public void setOrganisationSubService(List<Long> organisationSubService) {
        this.organisationSubService = organisationSubService;
    }
}
