package com.kairos.persistance.model.processing_activity.dto;

import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

public class MasterProcessingActivityDto {

    @NotNullOrEmpty(message = "error.message.name.cannotbe.null.or.empty")
    private  String name;

    @NotNullOrEmpty(message = "error.message.name.cannotbe.null.or.empty")
    private String description;

    @NotNull(message = "error.message.list.cannot.be.null")
    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private Set<Long> organisationType;

    @NotNull(message = "error.message.list.cannot.be.null")
    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private Set <Long> organisationSubType;

    @NotNull(message = "error.message.list.cannot.be.null")
    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private Set <Long>organisationService;

    @NotNull(message = "error.message.list.cannot.be.null")
    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private Set <Long> organisationSubService;

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

    public Set<Long> getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(Set<Long> organisationType) {
        this.organisationType = organisationType;
    }

    public Set<Long> getOrganisationSubType() {
        return organisationSubType;
    }

    public void setOrganisationSubType(Set<Long> organisationSubType) {
        this.organisationSubType = organisationSubType;
    }

    public Set<Long> getOrganisationService() {
        return organisationService;
    }

    public void setOrganisationService(Set<Long> organisationService) {
        this.organisationService = organisationService;
    }

    public Set<Long> getOrganisationSubService() {
        return organisationSubService;
    }

    public void setOrganisationSubService(Set<Long> organisationSubService) {
        this.organisationSubService = organisationSubService;
    }

    public MasterProcessingActivityDto()
    {

    }
}





