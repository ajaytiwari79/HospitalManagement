package com.kairos.response.dto.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.gdpr.AssetAssessor;
import com.kairos.persistence.model.embeddables.ManagingOrganization;
import com.kairos.persistence.model.embeddables.Staff;
import com.kairos.response.dto.common.*;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssetResponseDTO {

    private Long id;
    private String name;
    private String description;
    private String hostingLocation;
    private ManagingOrganization managingDepartment;
    private Staff assetOwner;
    private List<StorageFormatResponseDTO> storageFormats;
    private List<OrganizationalSecurityMeasureResponseDTO> orgSecurityMeasures;
    private List<TechnicalSecurityMeasureResponseDTO> technicalSecurityMeasures;
    private HostingProviderResponseDTO hostingProvider;
    private HostingTypeResponseDTO hostingType;
    private DataDisposalResponseDTO dataDisposal;
    private AssetTypeBasicResponseDTO assetType;
    private AssetTypeBasicResponseDTO subAssetType;
    private Integer dataRetentionPeriod;
    private Long minDataSubjectVolume;
    private Long maxDataSubjectVolume;
    private boolean active;
    private AssetAssessor assetAssessor;
    private boolean suggested;

    public boolean isSuggested() {
        return suggested;
    }

    public void setSuggested(boolean suggested) {
        this.suggested = suggested;
    }

    public AssetAssessor getAssetAssessor() {
        return assetAssessor;
    }

    public void setAssetAssessor(AssetAssessor assetAssessor) {
        this.assetAssessor = assetAssessor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getHostingLocation() {
        return hostingLocation;
    }

    public void setHostingLocation(String hostingLocation) {
        this.hostingLocation = hostingLocation;
    }

    public ManagingOrganization getManagingDepartment() {
        return managingDepartment;
    }

    public void setManagingDepartment(ManagingOrganization managingDepartment) {
        this.managingDepartment = managingDepartment;
    }

    public Staff getAssetOwner() {
        return assetOwner;
    }

    public void setAssetOwner(Staff assetOwner) {
        this.assetOwner = assetOwner;
    }

    public List<StorageFormatResponseDTO> getStorageFormats() {
        return storageFormats;
    }

    public void setStorageFormats(List<StorageFormatResponseDTO> storageFormats) {
        this.storageFormats = storageFormats;
    }

    public List<OrganizationalSecurityMeasureResponseDTO> getOrgSecurityMeasures() {
        return orgSecurityMeasures;
    }

    public void setOrgSecurityMeasures(List<OrganizationalSecurityMeasureResponseDTO> orgSecurityMeasures) {
        this.orgSecurityMeasures = orgSecurityMeasures;
    }

    public List<TechnicalSecurityMeasureResponseDTO> getTechnicalSecurityMeasures() {
        return technicalSecurityMeasures;
    }

    public void setTechnicalSecurityMeasures(List<TechnicalSecurityMeasureResponseDTO> technicalSecurityMeasures) {
        this.technicalSecurityMeasures = technicalSecurityMeasures;
    }

    public HostingProviderResponseDTO getHostingProvider() {
        return hostingProvider;
    }

    public void setHostingProvider(HostingProviderResponseDTO hostingProvider) {
        this.hostingProvider = hostingProvider;
    }

    public HostingTypeResponseDTO getHostingType() {
        return hostingType;
    }

    public void setHostingType(HostingTypeResponseDTO hostingType) {
        this.hostingType = hostingType;
    }

    public DataDisposalResponseDTO getDataDisposal() {
        return dataDisposal;
    }

    public void setDataDisposal(DataDisposalResponseDTO dataDisposal) {
        this.dataDisposal = dataDisposal;
    }

    public AssetTypeBasicResponseDTO getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetTypeBasicResponseDTO assetType) {
        this.assetType = assetType;
    }

    public AssetTypeBasicResponseDTO getSubAssetType() {
        return subAssetType;
    }

    public void setSubAssetType(AssetTypeBasicResponseDTO subAssetType) {
        this.subAssetType = subAssetType;
    }

    public Integer getDataRetentionPeriod() {
        return dataRetentionPeriod;
    }

    public void setDataRetentionPeriod(Integer dataRetentionPeriod) {
        this.dataRetentionPeriod = dataRetentionPeriod;
    }

    public Long getMinDataSubjectVolume() {
        return minDataSubjectVolume;
    }

    public void setMinDataSubjectVolume(Long minDataSubjectVolume) {
        this.minDataSubjectVolume = minDataSubjectVolume;
    }

    public Long getMaxDataSubjectVolume() {
        return maxDataSubjectVolume;
    }

    public void setMaxDataSubjectVolume(Long maxDataSubjectVolume) {
        this.maxDataSubjectVolume = maxDataSubjectVolume;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    }
