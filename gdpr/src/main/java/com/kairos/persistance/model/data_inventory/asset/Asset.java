package com.kairos.persistance.model.data_inventory.asset;


import com.kairos.enums.RiskSeverity;
import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.persistance.model.data_inventory.ManagingOrganization;
import com.kairos.persistance.model.data_inventory.Staff;
import com.kairos.utils.custom_annotation.NotNullOrEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

@Document(collection = "asset")
public class Asset extends MongoBaseEntity {


    @NotNullOrEmpty(message = "Name can 't be empty")
    private String name;

    @NotNullOrEmpty(message = "description can't be empty")
    private String description;

    private Long countryId;

    @NotBlank(message = "Hosting Loction can't be empty")
    private String hostingLocation;

    @NotNull(message = "Managing department can't be empty")
    private ManagingOrganization managingDepartment;

    @NotNull(message = "Asset Owner can't be Empty")
    private Staff assetOwner;

    private List<BigInteger> storageFormats;

    private List<BigInteger> orgSecurityMeasures;

    private List<BigInteger> technicalSecurityMeasures;

    private BigInteger processingActivity;

    private BigInteger hostingProvider;

    private BigInteger hostingType;

    private BigInteger dataDisposal;

    @NotNull(message = "Asset Type can't be empty")
    private BigInteger assetType;

    @NotEmpty(message = "Sub Asset Type can't be empty")
    private List<BigInteger> assetSubTypes;

    private Integer dataRetentionPeriod;

    private Long minDataSubjectVolume;

    private Long maxDataSubjectVolume;

    private RiskSeverity risk;

    private Boolean isActive=false;

    public Boolean getActive() { return isActive; }

    public void setActive(Boolean active) { isActive = active; }

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

    public BigInteger getHostingProvider() { return hostingProvider; }

    public void setHostingProvider(BigInteger hostingProvider) { this.hostingProvider = hostingProvider; }

    public BigInteger getHostingType() { return hostingType; }

    public void setHostingType(BigInteger hostingType) { this.hostingType = hostingType; }

    public BigInteger getAssetType() { return assetType; }

    public void setAssetType(BigInteger assetType) { this.assetType = assetType; }

    public Integer getDataRetentionPeriod() { return dataRetentionPeriod; }

    public void setDataRetentionPeriod(Integer dataRetentionPeriod) { this.dataRetentionPeriod = dataRetentionPeriod; }

    public Long getMinDataSubjectVolume() { return minDataSubjectVolume; }

    public void setMinDataSubjectVolume(Long minDataSubjectVolume) { this.minDataSubjectVolume = minDataSubjectVolume; }

    public Long getMaxDataSubjectVolume() { return maxDataSubjectVolume; }

    public void setMaxDataSubjectVolume(Long maxDataSubjectVolume) { this.maxDataSubjectVolume = maxDataSubjectVolume; }

    public RiskSeverity getRisk() { return risk; }

    public void setRisk(RiskSeverity risk) { this.risk = risk; }

    public BigInteger getProcessingActivity() { return processingActivity; }

    public void setProcessingActivity(BigInteger processingActivity) { this.processingActivity = processingActivity; }


    public String getHostingLocation() { return hostingLocation; }

    public void setHostingLocation(String hostingLocation) { this.hostingLocation = hostingLocation; }
    public ManagingOrganization getManagingDepartment() { return managingDepartment; }

    public void setManagingDepartment(ManagingOrganization managingDepartment) { this.managingDepartment = managingDepartment; }

    public Staff getAssetOwner() { return assetOwner; }

    public void setAssetOwner(Staff assetOwner) { this.assetOwner = assetOwner; }

    public Asset() {
    }

    public List<BigInteger> getAssetSubTypes() { return assetSubTypes; }

    public void setAssetSubTypes(List<BigInteger> assetSubTypes) { this.assetSubTypes = assetSubTypes; }

    public BigInteger getDataDisposal() { return dataDisposal; }

    public void setDataDisposal(BigInteger dataDisposal) { this.dataDisposal = dataDisposal; }

    public Asset(String name, String description, String hostingLocation, Long countryId, BigInteger assetType, List<BigInteger> assetSubTypes, ManagingOrganization managingDepartment, Staff assetOwner) {
        this.name = name;
        this.description = description;
        this.hostingLocation=hostingLocation;
        this.countryId = countryId;
        this.assetType = assetType;
        this.assetSubTypes=assetSubTypes;
        this.assetOwner=assetOwner;
        this.managingDepartment=managingDepartment;
    }
}


