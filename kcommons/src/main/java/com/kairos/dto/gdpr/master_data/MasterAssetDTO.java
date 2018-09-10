package com.kairos.dto.gdpr.master_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MasterAssetDTO {


    private BigInteger id;

    @NotBlank(message = "Name  can't be Empty")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "title can not contain number or special character")
    private String name;

    @NotBlank(message = "Description can't be Empty")
    private String description;

    @Valid
    @NotNull(message = "ManagingOrganization  Type  can't be  null")
    @NotEmpty(message = "ManagingOrganization Type  can't be  Empty")
    private List<OrganizationType> organizationTypes;

    @Valid
    @NotNull(message = "ManagingOrganization Sub Type  can't be  null")
    @NotEmpty(message = "ManagingOrganization Sub Type  can't be  Empty")
    private List<OrganizationSubType> organizationSubTypes;

    @Valid
    @NotNull(message = "Service  Type  can't be  null")
    @NotEmpty(message = "Service  Type  can't be  Empty")
    private List<ServiceCategory> organizationServices;

    @NotNull(message = "Service Sub Type  can't be  null")
    @NotEmpty(message = "Service Sub Type  can't be  Empty")
    @Valid
    private List<SubServiceCategory> organizationSubServices;

    @NotNull(message = "Asset Type Can't be empty")
    private BigInteger assetTypeId;

    private List<BigInteger> assetSubTypes;

    public List<BigInteger> getAssetSubTypes() { return assetSubTypes; }

    public void setAssetSubTypes(List<BigInteger> assetSubTypes) { this.assetSubTypes = assetSubTypes; }

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

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }
}
