package com.kairos.persistence.model.master_data.default_asset_setting;


import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.embeddables.OrganizationSubType;
import com.kairos.persistence.model.embeddables.OrganizationType;
import com.kairos.persistence.model.embeddables.ServiceCategory;
import com.kairos.persistence.model.embeddables.SubServiceCategory;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class MasterAssetMD extends BaseEntity {


    @NotBlank(message = "Name can't be empty")
    private  String name;
    @NotBlank(message = "error.message.name.cannotbe.null.or.empty")
    private String description;

    @ElementCollection
    private List<OrganizationType> organizationTypes = new ArrayList<>();

    @ElementCollection
    private List <OrganizationSubType> organizationSubTypes = new ArrayList<>();

    @ElementCollection
    private List <ServiceCategory> organizationServices = new ArrayList<>();

    @ElementCollection
    private List <SubServiceCategory> organizationSubServices = new ArrayList<>();

    private Long countryId;

    @OneToOne(fetch = FetchType.EAGER)
    private AssetTypeMD assetTypeMD;

    @OneToOne(fetch = FetchType.EAGER)
    private AssetTypeMD subAssetType;

    private LocalDate suggestedDate;
    private SuggestedDataStatus suggestedDataStatus;


    public MasterAssetMD(String name, String description, Long countryId, List<OrganizationType> organizationTypes,
                         List<OrganizationSubType> organizationSubTypes, List<ServiceCategory> organizationServices, List<SubServiceCategory> organizationSubServices, SuggestedDataStatus suggestedDataStatus) {
        this.name = name;
        this.description = description;
        this.countryId=countryId;
        this.organizationTypes = organizationTypes;
        this.organizationSubTypes = organizationSubTypes;
        this.organizationServices = organizationServices;
        this.organizationSubServices = organizationSubServices;
        this.suggestedDataStatus=suggestedDataStatus;

    }

    public MasterAssetMD(String name, String description, Long countryId, LocalDate suggestedDate, SuggestedDataStatus suggestedDataStatus) {
        this.name = name;
        this.description = description;
        this.countryId = countryId;
        this.suggestedDate = suggestedDate;
        this.suggestedDataStatus = suggestedDataStatus;
    }

    public MasterAssetMD(String name, String description, Long countryId, SuggestedDataStatus suggestedDataStatus) {
        this.name = name;
        this.description = description;
        this.countryId=countryId;
        this.suggestedDataStatus=suggestedDataStatus;

    }

    public LocalDate getSuggestedDate() { return suggestedDate; }

    public MasterAssetMD setSuggestedDate(LocalDate suggestedDate) { this.suggestedDate = suggestedDate; return this; }

    public SuggestedDataStatus getSuggestedDataStatus() { return suggestedDataStatus; }

    public MasterAssetMD setSuggestedDataStatus(SuggestedDataStatus suggestedDataStatus) { this.suggestedDataStatus = suggestedDataStatus;return this; }

    public Long getCountryId() { return countryId; }

    public MasterAssetMD setCountryId(Long countryId) { this.countryId = countryId; return this;}

    public String getName() {
        return name;
    }

    public MasterAssetMD setName(String name) { this.name = name;return this; }

    public String getDescription() {
        return description;
    }

    public MasterAssetMD setDescription(String description) { this.description = description;return this; }

    public List<OrganizationType> getOrganizationTypes() {
        return organizationTypes;
    }

    public MasterAssetMD setOrganizationTypes(List<OrganizationType> organizationTypes) { this.organizationTypes = organizationTypes; return this;}

    public List<OrganizationSubType> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public MasterAssetMD setOrganizationSubTypes(List<OrganizationSubType> organizationSubTypes) { this.organizationSubTypes = organizationSubTypes;return this; }

    public List<ServiceCategory> getOrganizationServices() {
        return organizationServices;
    }

    public MasterAssetMD setOrganizationServices(List<ServiceCategory> organizationServices) { this.organizationServices = organizationServices; return this;}

    public List<SubServiceCategory> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public MasterAssetMD setOrganizationSubServices(List<SubServiceCategory> organizationSubServices) { this.organizationSubServices = organizationSubServices; return this;}

    public AssetTypeMD getAssetType() {
        return assetTypeMD;
    }

    public void setAssetType(AssetTypeMD assetType) {
        this.assetTypeMD = assetType;
    }

    public AssetTypeMD getSubAssetType() {
        return subAssetType;
    }

    public void setSubAssetType(AssetTypeMD subAssetType) {
        this.subAssetType = subAssetType;
    }

    public MasterAssetMD() {
    }
}
