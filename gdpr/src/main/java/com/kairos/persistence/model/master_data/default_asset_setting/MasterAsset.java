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
public class MasterAsset extends BaseEntity {


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
    private AssetType assetType;
    @OneToOne(fetch = FetchType.EAGER)
    private AssetType subAssetType;
    private LocalDate suggestedDate;
    private SuggestedDataStatus suggestedDataStatus;


    public MasterAsset(String name, String description, Long countryId, List<OrganizationType> organizationTypes,
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

    public MasterAsset(String name, String description, Long countryId, LocalDate suggestedDate, SuggestedDataStatus suggestedDataStatus) {
        this.name = name;
        this.description = description;
        this.countryId = countryId;
        this.suggestedDate = suggestedDate;
        this.suggestedDataStatus = suggestedDataStatus;
    }

    public MasterAsset(String name, String description, Long countryId, SuggestedDataStatus suggestedDataStatus) {
        this.name = name;
        this.description = description;
        this.countryId=countryId;
        this.suggestedDataStatus=suggestedDataStatus;

    }

    public LocalDate getSuggestedDate() { return suggestedDate; }

    public MasterAsset setSuggestedDate(LocalDate suggestedDate) { this.suggestedDate = suggestedDate; return this; }

    public SuggestedDataStatus getSuggestedDataStatus() { return suggestedDataStatus; }

    public MasterAsset setSuggestedDataStatus(SuggestedDataStatus suggestedDataStatus) { this.suggestedDataStatus = suggestedDataStatus;return this; }

    public Long getCountryId() { return countryId; }

    public MasterAsset setCountryId(Long countryId) { this.countryId = countryId; return this;}

    public String getName() {
        return name;
    }

    public MasterAsset setName(String name) { this.name = name;return this; }

    public String getDescription() {
        return description;
    }

    public MasterAsset setDescription(String description) { this.description = description;return this; }

    public List<OrganizationType> getOrganizationTypes() {
        return organizationTypes;
    }

    public MasterAsset setOrganizationTypes(List<OrganizationType> organizationTypes) { this.organizationTypes = organizationTypes; return this;}

    public List<OrganizationSubType> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public MasterAsset setOrganizationSubTypes(List<OrganizationSubType> organizationSubTypes) { this.organizationSubTypes = organizationSubTypes;return this; }

    public List<ServiceCategory> getOrganizationServices() {
        return organizationServices;
    }

    public MasterAsset setOrganizationServices(List<ServiceCategory> organizationServices) { this.organizationServices = organizationServices; return this;}

    public List<SubServiceCategory> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public MasterAsset setOrganizationSubServices(List<SubServiceCategory> organizationSubServices) { this.organizationSubServices = organizationSubServices; return this;}

    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }

    public AssetType getSubAssetType() {
        return subAssetType;
    }

    public void setSubAssetType(AssetType subAssetType) {
        this.subAssetType = subAssetType;
    }

    public MasterAsset() {
    }
}
