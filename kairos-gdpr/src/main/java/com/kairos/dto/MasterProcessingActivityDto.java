package com.kairos.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

public class MasterProcessingActivityDto {

    @NotNullOrEmpty(message = "error.message.name.cannotbe.null.or.empty")
    private  String name;

    @NotNullOrEmpty(message = "error.message.name.cannotbe.null.or.empty")
    private String description;

    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private Set<Long> organizationTypes;

    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private Set <Long> organizationSubTypes;

    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private Set <Long>organizationServices;

    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private Set <Long> organizationSubServices;



    private List<MasterProcessingActivityDto> subProcessingActivities;



    public List<MasterProcessingActivityDto> getSubProcessingActivity() {
        return subProcessingActivities;
    }

    public void setSubProcessingActivity(List<MasterProcessingActivityDto> subProcessingActivities) {
        this.subProcessingActivities = subProcessingActivities;
    }

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

    public Set<Long> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(Set<Long> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public Set<Long> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(Set<Long> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public Set<Long> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(Set<Long> organizationServices) {
        this.organizationServices = organizationServices;
    }

    public Set<Long> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public void setOrganizationSubServices(Set<Long> organizationSubServices) {
        this.organizationSubServices = organizationSubServices;
    }

    public MasterProcessingActivityDto()
    {

    }
}





