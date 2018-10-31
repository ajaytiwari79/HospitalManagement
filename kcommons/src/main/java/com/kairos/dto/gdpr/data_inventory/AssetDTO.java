package com.kairos.dto.gdpr.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
    @NotNull(message = "error.message.assetType.notNull")
    private BigInteger assetType;
    private BigInteger assetSubTypeId;
    private Integer dataRetentionPeriod;
    private Long minDataSubjectVolume;
    private Long maxDataSubjectVolume;
    private RiskSeverity riskLevel;
    private AssetAssessor assetAssessor;
    private boolean suggested;

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
    public BigInteger getAssetType() { return assetType; }

    public BigInteger getAssetSubTypeId() { return assetSubTypeId; }

    public void setAssetSubTypeId(BigInteger assetSubTypeId) { this.assetSubTypeId = assetSubTypeId; }

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

    public void setAssetType(BigInteger assetType) { this.assetType = assetType; }


    public void setDataRetentionPeriod(Integer dataRetentionPeriod) { this.dataRetentionPeriod = dataRetentionPeriod; }

    public void setMinDataSubjectVolume(Long minDataSubjectVolume) { this.minDataSubjectVolume = minDataSubjectVolume; }

    public void setMaxDataSubjectVolume(Long maxDataSubjectVolume) { this.maxDataSubjectVolume = maxDataSubjectVolume; }

    public void setAssetAssessor(AssetAssessor assetAssessor) { this.assetAssessor = assetAssessor; }

    public AssetDTO() {
    }
}
