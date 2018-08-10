package com.kairos.dto.master_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.OrganizationSubTypeDTO;
import com.kairos.dto.OrganizationTypeDTO;
import com.kairos.dto.ServiceCategoryDTO;
import com.kairos.dto.SubServiceCategoryDTO;
import com.kairos.utils.custom_annotation.NotNullOrEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterProcessingActivityDTO {

    private BigInteger id;

    @NotNullOrEmpty(message = "Name  can't be Empty")
    @Pattern(regexp = "^[a-zA-Z\\s]+$",message = "title can not contain number or special character")
    private  String name;

    @NotNullOrEmpty(message = "Description  can't be  Empty")
    private String description;

    @NotEmpty(message = "ManagingOrganization Type  can't be  Empty")
    @NotNull(message = "ManagingOrganization  Type  can't be  null")
    @Valid
    private List<OrganizationTypeDTO> organizationTypes;

    @NotNull(message = "ManagingOrganization Sub Type  can't be  null")
    @NotEmpty(message = "ManagingOrganization Sub Type  can't be Empty")
    @Valid
    private List<OrganizationSubTypeDTO> organizationSubTypes;

    @NotNull(message = "Service Type  can't be  null")
    @NotEmpty(message = "Service Type  can't be  Empty")
    @Valid
    private List<ServiceCategoryDTO> organizationServices;

    @NotNull(message = "Service Sub Type  can't be  null")
    @NotEmpty(message = "Service Sub Type  can't be  Empty")
    @Valid
    private List<SubServiceCategoryDTO> organizationSubServices;

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

    public List<OrganizationTypeDTO> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<OrganizationTypeDTO> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public List<OrganizationSubTypeDTO> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(List<OrganizationSubTypeDTO> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public List<ServiceCategoryDTO> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(List<ServiceCategoryDTO> organizationServices) {
        this.organizationServices = organizationServices;
    }

    public List<SubServiceCategoryDTO> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public void setOrganizationSubServices(List<SubServiceCategoryDTO> organizationSubServices) {
        this.organizationSubServices = organizationSubServices;
    }

    public MasterProcessingActivityDTO()
    {

    }
}





