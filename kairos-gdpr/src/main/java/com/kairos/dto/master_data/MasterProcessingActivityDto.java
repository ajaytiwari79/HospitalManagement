package com.kairos.dto.master_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.OrganizationTypeAndServiceBasicDto;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterProcessingActivityDto {

    @NotNullOrEmpty(message = "Name  can't be Empty")
    @Pattern(regexp = "^[a-zA-Z\\s]+$",message = "title can not contain number or special character")
    private  String name;

    @NotNullOrEmpty(message = "Description  can't be  Empty")
    private String description;

    @NotEmpty(message = "Organization Type  can't be  Empty")
    @NotNull(message = "Organization  Type  can't be  null")
    @Valid
    private List<OrganizationTypeAndServiceBasicDto> organizationTypes;

    @NotNull(message = "Organization Sub Type  can't be  null")
    @NotEmpty(message = "Organization Sub Type  can't be Empty")
    @Valid
    private List<OrganizationTypeAndServiceBasicDto> organizationSubTypes;

    @NotNull(message = "Service Type  can't be  null")
    @NotEmpty(message = "Service Type  can't be  Empty")
    @Valid
    private List<OrganizationTypeAndServiceBasicDto> organizationServices;

    @NotNull(message = "Service Sub Type  can't be  null")
    @NotEmpty(message = "Service Sub Type  can't be  Empty")
    @Valid
    private List<OrganizationTypeAndServiceBasicDto> organizationSubServices;

    private Boolean isSubProcess=false;

    private List<MasterProcessingActivityDto> subProcessingActivities;


    public List<MasterProcessingActivityDto> getSubProcessingActivities() {
        return subProcessingActivities;
    }

    public Boolean getSubProcess() {
        return isSubProcess;
    }

    public void setSubProcess(Boolean subProcess) {
        isSubProcess = subProcess;
    }

    public void setSubProcessingActivities(List<MasterProcessingActivityDto> subProcessingActivities) {
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

    public List<OrganizationTypeAndServiceBasicDto> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<OrganizationTypeAndServiceBasicDto> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public List<OrganizationTypeAndServiceBasicDto> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(List<OrganizationTypeAndServiceBasicDto> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public List<OrganizationTypeAndServiceBasicDto> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(List<OrganizationTypeAndServiceBasicDto> organizationServices) {
        this.organizationServices = organizationServices;
    }

    public List<OrganizationTypeAndServiceBasicDto> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public void setOrganizationSubServices(List<OrganizationTypeAndServiceBasicDto> organizationSubServices) {
        this.organizationSubServices = organizationSubServices;
    }

    public MasterProcessingActivityDto()
    {

    }
}





