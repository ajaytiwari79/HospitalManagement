package com.kairos.dto.master_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MasterAssetDTO {

    @NotNullOrEmpty(message = "Name  can't be Empty")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "title can not contain number or special character")
    private String name;

    @NotNullOrEmpty(message = "Description can't be Empty")
    private String description;

    @Valid
    @NotNull(message = "ManagingOrganization  Type  can't be  null")
    @NotEmpty(message = "ManagingOrganization Type  can't be  Empty")
    private List<OrganizationTypeDTO> organizationTypes;

    @Valid
    @NotNull(message = "ManagingOrganization Sub Type  can't be  null")
    @NotEmpty(message = "ManagingOrganization Sub Type  can't be  Empty")
    private List<OrganizationSubTypeDTO> organizationSubTypes;

    @Valid
    @NotNull(message = "Service  Type  can't be  null")
    @NotEmpty(message = "Service  Type  can't be  Empty")
    private List<ServiceCategoryDTO> organizationServices;

    @NotNull(message = "Service Sub Type  can't be  null")
    @NotEmpty(message = "Service Sub Type  can't be  Empty")
    @Valid
    private List<SubServiceCategoryDTO> organizationSubServices;

    @NotNull(message = "Asset Type Can't be empty")
    private BigInteger assetTypeId;

    public BigInteger getAssetTypeId() {
        return assetTypeId;
    }

    public void setAssetTypeId(BigInteger assetTypeId) {
        this.assetTypeId = assetTypeId;
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
}
