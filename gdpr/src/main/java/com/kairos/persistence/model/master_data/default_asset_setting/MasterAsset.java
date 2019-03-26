package com.kairos.persistence.model.master_data.default_asset_setting;


import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.embeddables.OrganizationSubType;
import com.kairos.persistence.model.embeddables.OrganizationType;
import com.kairos.persistence.model.embeddables.ServiceCategory;
import com.kairos.persistence.model.embeddables.SubServiceCategory;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class MasterAsset extends BaseEntity {


    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String name;
    @NotBlank(message = "error.message.description.notNull.orEmpty")
    private String description;
    @ElementCollection
    private List<OrganizationType> organizationTypes = new ArrayList<>();
    @ElementCollection
    private List<OrganizationSubType> organizationSubTypes = new ArrayList<>();
    @ElementCollection
    private List<ServiceCategory> organizationServices = new ArrayList<>();
    @ElementCollection
    private List<SubServiceCategory> organizationSubServices = new ArrayList<>();
    private Long countryId;
    @OneToOne(fetch = FetchType.EAGER)
    private AssetType assetType;
    @OneToOne(fetch = FetchType.EAGER)
    private AssetType subAssetType;
    private LocalDate suggestedDate;
    private SuggestedDataStatus suggestedDataStatus;


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
        this.countryId = countryId;
        this.suggestedDataStatus = suggestedDataStatus;

    }

    public void setName(String name) { this.name = name; }

    public void setDescription(String description) { this.description = description; }

    public void setOrganizationTypes(List<OrganizationType> organizationTypes) { this.organizationTypes = organizationTypes; }

    public void setOrganizationSubTypes(List<OrganizationSubType> organizationSubTypes) { this.organizationSubTypes = organizationSubTypes; }

    public void setOrganizationServices(List<ServiceCategory> organizationServices) { this.organizationServices = organizationServices; }

    public void setOrganizationSubServices(List<SubServiceCategory> organizationSubServices) { this.organizationSubServices = organizationSubServices; }

    public void setCountryId(Long countryId) { this.countryId = countryId; }

    public void setSuggestedDate(LocalDate suggestedDate) { this.suggestedDate = suggestedDate; }

    public void setSuggestedDataStatus(SuggestedDataStatus suggestedDataStatus) { this.suggestedDataStatus = suggestedDataStatus; }

    public LocalDate getSuggestedDate() {
        return suggestedDate;
    }

    public SuggestedDataStatus getSuggestedDataStatus() {
        return suggestedDataStatus;
    }

    public Long getCountryId() {
        return countryId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<OrganizationType> getOrganizationTypes() {
        return organizationTypes;
    }

    public List<OrganizationSubType> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public List<ServiceCategory> getOrganizationServices() {
        return organizationServices;
    }

    public List<SubServiceCategory> getOrganizationSubServices() {
        return organizationSubServices;
    }

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
