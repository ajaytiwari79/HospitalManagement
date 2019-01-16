package com.kairos.persistence.model.data_inventory.asset;


import com.kairos.enums.gdpr.AssetAssessor;
import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.embeddables.ManagingOrganization;
import com.kairos.persistence.model.embeddables.Staff;
import com.kairos.persistence.model.master_data.default_asset_setting.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class AssetMD extends BaseEntity {


    @NotBlank(message = "Name can 't be empty")
    private String name;
    @NotBlank(message = "description can't be empty")
    private String description;
    private Long countryId;
    private String hostingLocation;

    @Embedded
    private ManagingOrganization managingDepartment;

    @Embedded
    private Staff assetOwner;

    @OneToMany(fetch = FetchType.LAZY)
    private List<StorageFormatMD> storageFormats  = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    private List<OrganizationalSecurityMeasureMD> orgSecurityMeasures  = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    private List<TechnicalSecurityMeasureMD> technicalSecurityMeasures  = new ArrayList<>();

    @OneToOne
    private HostingProviderMD hostingProvider;

    @OneToOne
    private HostingTypeMD hostingType;

    @OneToOne
    private DataDisposalMD dataDisposal;

    @OneToOne
    private AssetTypeMD assetType;

    @OneToOne
    private AssetTypeMD subAssetType;

   /* private Set<BigInteger> processingActivityIds;
    private Set<BigInteger> subProcessingActivityIds;*/
    private Integer dataRetentionPeriod;
    @NotNull(message = "Status can't be empty")
    private boolean active=true;
    private boolean suggested;

    private AssetAssessor assetAssessor;


    public AssetMD() {
    }

    public AssetMD(String name, String description, String hostingLocation, ManagingOrganization managingDepartment, Staff assetOwner) {
        this.name = name;
        this.description = description;
        this.hostingLocation=hostingLocation;
        this.assetOwner=assetOwner;
        this.managingDepartment=managingDepartment;
    }


    public AssetMD(String name, String description, boolean active) {
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

    public Integer getDataRetentionPeriod() { return dataRetentionPeriod; }

    public void setDataRetentionPeriod(Integer dataRetentionPeriod) { this.dataRetentionPeriod = dataRetentionPeriod; }

    public String getHostingLocation() { return hostingLocation; }

    public void setHostingLocation(String hostingLocation) { this.hostingLocation = hostingLocation; }
    public ManagingOrganization getManagingDepartment() { return managingDepartment; }

    public void setManagingDepartment(ManagingOrganization managingDepartment) { this.managingDepartment = managingDepartment; }

    public Staff getAssetOwner() { return assetOwner; }

    public void setAssetOwner(Staff assetOwner) { this.assetOwner = assetOwner; }

    public List<StorageFormatMD> getStorageFormats() {
        return storageFormats;
    }

    public void setStorageFormats(List<StorageFormatMD> storageFormats) {
        this.storageFormats = storageFormats;
    }

    public List<OrganizationalSecurityMeasureMD> getOrgSecurityMeasures() {
        return orgSecurityMeasures;
    }

    public void setOrgSecurityMeasures(List<OrganizationalSecurityMeasureMD> orgSecurityMeasures) {
        this.orgSecurityMeasures = orgSecurityMeasures;
    }

    public List<TechnicalSecurityMeasureMD> getTechnicalSecurityMeasures() {
        return technicalSecurityMeasures;
    }

    public void setTechnicalSecurityMeasures(List<TechnicalSecurityMeasureMD> technicalSecurityMeasures) {
        this.technicalSecurityMeasures = technicalSecurityMeasures;
    }

    public HostingProviderMD getHostingProvider() {
        return hostingProvider;
    }

    public void setHostingProvider(HostingProviderMD hostingProvider) {
        this.hostingProvider = hostingProvider;
    }

    public HostingTypeMD getHostingType() {
        return hostingType;
    }

    public void setHostingType(HostingTypeMD hostingType) {
        this.hostingType = hostingType;
    }

    public DataDisposalMD getDataDisposal() {
        return dataDisposal;
    }

    public void setDataDisposal(DataDisposalMD dataDisposal) {
        this.dataDisposal = dataDisposal;
    }

    public AssetTypeMD getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetTypeMD assetType) {
        this.assetType = assetType;
    }

    public AssetTypeMD getSubAssetType() {
        return subAssetType;
    }

    public void setSubAssetType(AssetTypeMD subAssetType) {
        this.subAssetType = subAssetType;
    }
}


