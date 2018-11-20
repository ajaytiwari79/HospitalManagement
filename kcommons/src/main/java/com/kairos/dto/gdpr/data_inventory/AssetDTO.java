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

    private BigInteger id;
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
    private List<BigInteger> storageFormats;
    private List<BigInteger> orgSecurityMeasures;
    private List<BigInteger> technicalSecurityMeasures;
    private BigInteger processingActivity;
    private BigInteger hostingProvider;
    private BigInteger hostingType;
    private BigInteger dataDisposal;

    private Integer dataRetentionPeriod;
    private Long minDataSubjectVolume;
    private Long maxDataSubjectVolume;
    private RiskSeverity riskLevel;
    private AssetAssessor assetAssessor;
    private boolean suggested;
    @NotNull(message = "error.message.assetType.notNull")
    private AssetTypeDTO assetType;
    private AssetTypeDTO assetSubType;
    private Set<BigInteger> processingActivityIds;
    private Set<BigInteger> subProcessingActivityIds;



    public boolean isSuggested() { return suggested; }

    public void setSuggested(boolean suggested) { this.suggested = suggested; }


    public AssetAssessor getAssetAssessor() { return assetAssessor; }

    public void setId(BigInteger id) { this.id = id; }

    public BigInteger getId() { return id; }

    public String getName() { return name.trim(); }

    public String getDescription() { return description; }

    public String getHostingLocation() { return hostingLocation; }

    public ManagingOrganization getManagingDepartment() { return managingDepartment; }

    public Staff getAssetOwner() { return assetOwner; }

    public List<BigInteger> getStorageFormats() { return storageFormats; }

    public List<BigInteger> getOrgSecurityMeasures() { return orgSecurityMeasures; }

    public List<BigInteger> getTechnicalSecurityMeasures() { return technicalSecurityMeasures; }

    public BigInteger getProcessingActivity() { return processingActivity; }

    public BigInteger getHostingProvider() { return hostingProvider; }

    public BigInteger getHostingType() { return hostingType; }

    public BigInteger getDataDisposal() { return dataDisposal; }

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

    public void setStorageFormats(List<BigInteger> storageFormats) { this.storageFormats = storageFormats; }

    public void setOrgSecurityMeasures(List<BigInteger> orgSecurityMeasures) { this.orgSecurityMeasures = orgSecurityMeasures; }

    public void setTechnicalSecurityMeasures(List<BigInteger> technicalSecurityMeasures) { this.technicalSecurityMeasures = technicalSecurityMeasures; }

    public void setProcessingActivity(BigInteger processingActivity) { this.processingActivity = processingActivity; }

    public void setHostingProvider(BigInteger hostingProvider) { this.hostingProvider = hostingProvider; }

    public void setHostingType(BigInteger hostingType) { this.hostingType = hostingType; }
    public void setDataDisposal(BigInteger dataDisposal) { this.dataDisposal = dataDisposal; }

    public void setDataRetentionPeriod(Integer dataRetentionPeriod) { this.dataRetentionPeriod = dataRetentionPeriod; }

    public void setMinDataSubjectVolume(Long minDataSubjectVolume) { this.minDataSubjectVolume = minDataSubjectVolume; }

    public void setMaxDataSubjectVolume(Long maxDataSubjectVolume) { this.maxDataSubjectVolume = maxDataSubjectVolume; }

    public void setAssetAssessor(AssetAssessor assetAssessor) { this.assetAssessor = assetAssessor; }

    public AssetTypeDTO getAssetType() { return assetType; }

    public void setAssetType(AssetTypeDTO assetType) { this.assetType = assetType; }

    public AssetTypeDTO getAssetSubType() { return assetSubType; }

    public void setAssetSubType(AssetTypeDTO assetSubType) { this.assetSubType = assetSubType; }

    public Set<BigInteger> getProcessingActivityIds() { return processingActivityIds; }

    public void setProcessingActivityIds(Set<BigInteger> processingActivityIds) { this.processingActivityIds = processingActivityIds; }

    public Set<BigInteger> getSubProcessingActivityIds() { return subProcessingActivityIds; }

    public void setSubProcessingActivityIds(Set<BigInteger> subProcessingActivityIds) { this.subProcessingActivityIds = subProcessingActivityIds; }

    public AssetDTO() {
    }

}
