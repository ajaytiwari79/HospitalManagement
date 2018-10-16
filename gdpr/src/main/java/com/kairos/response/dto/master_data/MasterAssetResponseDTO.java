package com.kairos.response.dto.master_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.OrganizationSubType;
import com.kairos.dto.gdpr.OrganizationType;
import com.kairos.dto.gdpr.ServiceCategory;
import com.kairos.dto.gdpr.SubServiceCategory;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.response.dto.common.AssetTypeBasicResponseDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterAssetResponseDTO {

    @NotNull
    private BigInteger id;
    @NotBlank(message = "Name can't be empty")
    private String name;
    @NotBlank(message = "Description can't be empty")
    private String description;
    private List<OrganizationType> organizationTypes;
    private List<OrganizationSubType> organizationSubTypes;
    private List<ServiceCategory> organizationServices;
    private List<SubServiceCategory> organizationSubServices;
    private AssetTypeBasicResponseDTO assetType;
    public List<AssetTypeBasicResponseDTO> assetSubTypes;
    private LocalDate suggestedDate;
    private SuggestedDataStatus suggestedDataStatus;


    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

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

    public List<OrganizationType> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<OrganizationType> organizationTypes) { this.organizationTypes = organizationTypes; }

    public List<OrganizationSubType> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(List<OrganizationSubType> organizationSubTypes) { this.organizationSubTypes = organizationSubTypes; }

    public List<ServiceCategory> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(List<ServiceCategory> organizationServices) { this.organizationServices = organizationServices; }

    public List<SubServiceCategory> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public void setOrganizationSubServices(List<SubServiceCategory> organizationSubServices) { this.organizationSubServices = organizationSubServices; }

    public LocalDate getSuggestedDate() { return suggestedDate; }

    public void setSuggestedDate(LocalDate suggestedDate) { this.suggestedDate = suggestedDate; }

    public SuggestedDataStatus getSuggestedDataStatus() { return suggestedDataStatus; }

    public void setSuggestedDataStatus(SuggestedDataStatus suggestedDataStatus) { this.suggestedDataStatus = suggestedDataStatus; }
}
