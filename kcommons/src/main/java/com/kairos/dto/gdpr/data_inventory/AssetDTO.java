package com.kairos.dto.gdpr.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.gdpr.master_data.AssetTypeDTO;
import com.kairos.enums.RiskSeverity;
import com.kairos.dto.gdpr.ManagingOrganization;
import com.kairos.dto.gdpr.Staff;
import com.kairos.enums.gdpr.AssetAssessor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssetDTO {

    private Long id;
    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String name;
    @NotBlank(message = "error.message.description.notNull.orEmpty")
    private String description;
    @NotBlank(message = "error.message.HostingLocation.notSelected")
    private String hostingLocation;
    @NotNull(message = "error.message.managingDepartment.notNull")
    private ManagingOrganization managingDepartment;
    @NotNull(message = "error.message.assetOwner.notNull")
    private Staff assetOwner;
    private Set<Long> storageFormats;
    private Set<Long> orgSecurityMeasures;
    private Set<Long> technicalSecurityMeasures;
    private Long processingActivity;
    private Long hostingProvider;
    private Long hostingType;
    private Long dataDisposal;

    private Integer dataRetentionPeriod;
    private Long minDataSubjectVolume;
    private Long maxDataSubjectVolume;
    private RiskSeverity riskLevel;
    private AssetAssessor assetAssessor;
    private boolean suggested;
    @NotNull(message = "error.message.assetType.notNull")
    private AssetTypeOrganizationLevelDTO assetType;
    private AssetTypeOrganizationLevelDTO assetSubType;
    private Set<BigInteger> processingActivityIds;
    private Set<BigInteger> subProcessingActivityIds;



    public boolean isSuggested() { return suggested; }

    public void setSuggested(boolean suggested) { this.suggested = suggested; }


    public AssetAssessor getAssetAssessor() { return assetAssessor; }

    public void setId(Long id) { this.id = id; }

    public Long getId() { return id; }

    public String getName() { return name.trim(); }

    public String getDescription() { return description; }

    public String getHostingLocation() { return hostingLocation; }

    public ManagingOrganization getManagingDepartment() { return managingDepartment; }

    public Staff getAssetOwner() { return assetOwner; }

    public Set<Long> getStorageFormats() { return storageFormats; }

    public Set<Long> getOrgSecurityMeasures() { return orgSecurityMeasures; }

    public Set<Long> getTechnicalSecurityMeasures() { return technicalSecurityMeasures; }

    public Long getProcessingActivity() { return processingActivity; }

    public Long getHostingProvider() { return hostingProvider; }

    public Long getHostingType() { return hostingType; }

    public Long getDataDisposal() { return dataDisposal; }

    public Integer getDataRetentionPeriod() { return dataRetentionPeriod; }

    public Long getMinDataSubjectVolume() { return minDataSubjectVolume; }

    public Long getMaxDataSubjectVolume() { return maxDataSubjectVolume; }

    public RiskSeverity getRiskLevel() { return riskLevel; }

    public void setRiskLevel(RiskSeverity riskLevel) { this.riskLevel = riskLevel; }

    public void setName(String name) { this.name = name; }

    public void setDescription(String description) { this.description = description; }

    public void setHostingLocation(String hostingLocation) { this.hostingLocation = hostingLocation; }

    public void setManagingDepartment(ManagingOrganization managingDepartment) { this.managingDepartment = managingDepartment; }

    public void setAssetOwner(Staff assetOwner) { this.assetOwner = assetOwner; }

    public void setStorageFormats(Set<Long> storageFormats) { this.storageFormats = storageFormats; }

    public void setOrgSecurityMeasures(Set<Long> orgSecurityMeasures) { this.orgSecurityMeasures = orgSecurityMeasures; }

    public void setTechnicalSecurityMeasures(Set<Long> technicalSecurityMeasures) { this.technicalSecurityMeasures = technicalSecurityMeasures; }

    public void setProcessingActivity(Long processingActivity) { this.processingActivity = processingActivity; }

    public void setHostingProvider(Long hostingProvider) { this.hostingProvider = hostingProvider; }

    public void setHostingType(Long hostingType) { this.hostingType = hostingType; }
    public void setDataDisposal(Long dataDisposal) { this.dataDisposal = dataDisposal; }

    public void setDataRetentionPeriod(Integer dataRetentionPeriod) { this.dataRetentionPeriod = dataRetentionPeriod; }

    public void setMinDataSubjectVolume(Long minDataSubjectVolume) { this.minDataSubjectVolume = minDataSubjectVolume; }

    public void setMaxDataSubjectVolume(Long maxDataSubjectVolume) { this.maxDataSubjectVolume = maxDataSubjectVolume; }

    public void setAssetAssessor(AssetAssessor assetAssessor) { this.assetAssessor = assetAssessor; }

    public AssetTypeOrganizationLevelDTO getAssetType() { return assetType; }

    public void setAssetType(AssetTypeOrganizationLevelDTO assetType) { this.assetType = assetType; }

    public AssetTypeOrganizationLevelDTO getAssetSubType() { return assetSubType; }

    public void setAssetSubType(AssetTypeOrganizationLevelDTO assetSubType) { this.assetSubType = assetSubType; }

    public Set<BigInteger> getProcessingActivityIds() { return processingActivityIds; }

    public void setProcessingActivityIds(Set<BigInteger> processingActivityIds) { this.processingActivityIds = processingActivityIds; }

    public Set<BigInteger> getSubProcessingActivityIds() { return subProcessingActivityIds; }

    public void setSubProcessingActivityIds(Set<BigInteger> subProcessingActivityIds) { this.subProcessingActivityIds = subProcessingActivityIds; }

    public AssetDTO() {
    }

}
