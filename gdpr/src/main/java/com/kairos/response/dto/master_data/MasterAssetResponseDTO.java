package com.kairos.response.dto.master_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.OrganizationSubTypeDTO;
import com.kairos.dto.OrganizationTypeDTO;
import com.kairos.dto.ServiceCategoryDTO;
import com.kairos.dto.SubServiceCategoryDTO;
import com.kairos.response.dto.common.AssetTypeBasicResponseDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterAssetResponseDTO {

    @NotNull
    private BigInteger id;

    @NotBlank(message = "Name can't be empty")
    private String name;

    @NotBlank(message = "Description can't be empty")
    private String description;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    private List<OrganizationTypeDTO> organizationTypes;

    private List<OrganizationSubTypeDTO> organizationSubTypes;

    private List<ServiceCategoryDTO> organizationServices;

    private List<SubServiceCategoryDTO> organizationSubServices;

    private AssetTypeBasicResponseDTO assetType;

    public List<AssetTypeBasicResponseDTO> assetSubTypes;


    public AssetTypeBasicResponseDTO getAssetType() { return assetType; }

    public void setAssetType(AssetTypeBasicResponseDTO assetType) { this.assetType = assetType; }

    public List<AssetTypeBasicResponseDTO> getAssetSubTypes() { return assetSubTypes; }

    public void setAssetSubTypes(List<AssetTypeBasicResponseDTO> assetSubTypes) { this.assetSubTypes = assetSubTypes; }

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
