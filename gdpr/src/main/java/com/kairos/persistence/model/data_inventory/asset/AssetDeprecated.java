package com.kairos.persistence.model.data_inventory.asset;


import com.kairos.enums.gdpr.AssetAssessor;
import com.kairos.dto.gdpr.ManagingOrganization;
import com.kairos.dto.gdpr.Staff;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;


public class AssetDeprecated {

    @NotBlank(message = "Name can 't be empty")
    private String name;
    @NotBlank(message = "description can't be empty")
    private String description;
    private Long countryId;
    private String hostingLocation;
    private ManagingOrganization managingDepartment;
    private Staff assetOwner;
    private List<BigInteger> storageFormats;
    private List<BigInteger> orgSecurityMeasures;
    private List<BigInteger> technicalSecurityMeasures;
    private BigInteger hostingProviderId;
    private BigInteger hostingTypeId;
    private BigInteger dataDisposalId;
    private BigInteger assetTypeId;
    private BigInteger assetSubTypeId;
    private Set<BigInteger> processingActivityIds;
    private Set<BigInteger> subProcessingActivityIds;
    private Integer dataRetentionPeriod;
    @NotNull(message = "Status can't be empty")
    private boolean active=true;
    private boolean suggested;
    private AssetAssessor assetAssessor;


    public AssetDeprecated() {
    }

    public AssetDeprecated(String name, String description, String hostingLocation, ManagingOrganization managingDepartment, Staff assetOwner) {
        this.name = name;
        this.description = description;
        this.hostingLocation=hostingLocation;
        this.assetOwner=assetOwner;
        this.managingDepartment=managingDepartment;
    }


    public AssetDeprecated(String name, String description, boolean active) {
        this.name = name;
        this.description = description;
        this.active = active;
    }

    public boolean isSuggested() { return suggested; }

    public void setSuggested(boolean suggested) { this.suggested = suggested; }

    public AssetAssessor getAssetAssessor() { return assetAssessor; }

    public void setAssetAssessor(AssetAssessor assetAssessor) { this.assetAssessor = assetAssessor; }

    public boolean isActive() { return active; }

    public void setActive(boolean active) { this.active = active; }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCountryId() { return countryId; }

    public void setCountryId(Long countryId) { this.countryId = countryId; }

    public List<BigInteger> getStorageFormats() { return storageFormats; }

    public void setStorageFormats(List<BigInteger> storageFormats) { this.storageFormats = storageFormats; }

    public List<BigInteger> getOrgSecurityMeasures() { return orgSecurityMeasures; }

    public void setOrgSecurityMeasures(List<BigInteger> orgSecurityMeasures) { this.orgSecurityMeasures = orgSecurityMeasures; }

    public List<BigInteger> getTechnicalSecurityMeasures() { return technicalSecurityMeasures; }

    public void setTechnicalSecurityMeasures(List<BigInteger> technicalSecurityMeasures) { this.technicalSecurityMeasures = technicalSecurityMeasures; }


    public Integer getDataRetentionPeriod() { return dataRetentionPeriod; }

    public void setDataRetentionPeriod(Integer dataRetentionPeriod) { this.dataRetentionPeriod = dataRetentionPeriod; }

    public String getHostingLocation() { return hostingLocation; }

    public void setHostingLocation(String hostingLocation) { this.hostingLocation = hostingLocation; }
    public ManagingOrganization getManagingDepartment() { return managingDepartment; }

    public void setManagingDepartment(ManagingOrganization managingDepartment) { this.managingDepartment = managingDepartment; }

    public Staff getAssetOwner() { return assetOwner; }

    public void setAssetOwner(Staff assetOwner) { this.assetOwner = assetOwner; }

    public BigInteger getHostingProviderId() { return hostingProviderId; }

    public void setHostingProviderId(BigInteger hostingProviderId) { this.hostingProviderId = hostingProviderId; }

    public BigInteger getHostingTypeId() { return hostingTypeId; }

    public void setHostingTypeId(BigInteger hostingTypeId) { this.hostingTypeId = hostingTypeId; }

    public BigInteger getDataDisposalId() { return dataDisposalId; }

    public void setDataDisposalId(BigInteger dataDisposalId) { this.dataDisposalId = dataDisposalId; }

    public BigInteger getAssetTypeId() { return assetTypeId; }

    public void setAssetTypeId(BigInteger assetTypeId) { this.assetTypeId = assetTypeId; }

    public BigInteger getAssetSubTypeId() { return assetSubTypeId; }

    public void setAssetSubTypeId(BigInteger assetSubTypeId) { this.assetSubTypeId = assetSubTypeId; }

    public Set<BigInteger> getProcessingActivityIds() { return processingActivityIds; }

    public void setProcessingActivityIds(Set<BigInteger> processingActivityIds) { this.processingActivityIds = processingActivityIds; }

    public Set<BigInteger> getSubProcessingActivityIds() { return subProcessingActivityIds; }

    public void setSubProcessingActivityIds(Set<BigInteger> subProcessingActivityIds) { this.subProcessingActivityIds = subProcessingActivityIds; }
}


