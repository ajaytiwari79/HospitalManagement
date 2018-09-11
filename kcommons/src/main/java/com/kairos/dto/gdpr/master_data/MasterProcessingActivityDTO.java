package com.kairos.dto.gdpr.master_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.OrganizationSubType;
import com.kairos.dto.gdpr.OrganizationType;
import com.kairos.dto.gdpr.ServiceCategory;
import com.kairos.dto.gdpr.SubServiceCategory;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterProcessingActivityDTO {

    private BigInteger id;

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private  String name;

    @NotBlank(message = "error.message.description.notNull.orEmpty")
    private String description;

    @NotEmpty(message = "ManagingOrganization Type  can't be  Empty")
    @NotNull(message = "ManagingOrganization  Type  can't be  null")
    @Valid
    private List<OrganizationType> organizationTypes;

    @NotNull(message = "ManagingOrganization Sub Type  can't be  null")
    @NotEmpty(message = "ManagingOrganization Sub Type  can't be Empty")
    @Valid
    private List<OrganizationSubType> organizationSubTypes;

    @NotNull(message = "Service Type  can't be  null")
    @NotEmpty(message = "Service Type  can't be  Empty")
    @Valid
    private List<ServiceCategory> organizationServices;

    @NotNull(message = "Service Sub Type  can't be  null")
    @NotEmpty(message = "Service Sub Type  can't be  Empty")
    @Valid
    private List<SubServiceCategory> organizationSubServices;

    private List<MasterProcessingActivityDTO> subProcessingActivities;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public List<MasterProcessingActivityDTO> getSubProcessingActivities() {
        return subProcessingActivities;
    }

   public void setSubProcessingActivities(List<MasterProcessingActivityDTO> subProcessingActivities) {
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

    public List<OrganizationType> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<OrganizationType> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public List<OrganizationSubType> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(List<OrganizationSubType> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public List<ServiceCategory> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(List<ServiceCategory> organizationServices) {
        this.organizationServices = organizationServices;
    }

    public List<SubServiceCategory> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public void setOrganizationSubServices(List<SubServiceCategory> organizationSubServices) {
        this.organizationSubServices = organizationSubServices;
    }

    public MasterProcessingActivityDTO()
    {

    }
}





