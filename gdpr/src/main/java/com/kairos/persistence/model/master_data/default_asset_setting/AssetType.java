package com.kairos.persistence.model.master_data.default_asset_setting;

import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.risk_management.Risk;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
public class AssetType extends BaseEntity {

    @NotBlank(message = "Name can't be empty or null")
    @Pattern(message = "Numbers and Special characters are not allowed for Name", regexp = "^[a-zA-Z\\s]+$")
    private String name;
    private Long countryId;
    private Long organizationId;
    private boolean subAssetType;
    private boolean hasSubAsset;
    private SuggestedDataStatus suggestedDataStatus;
    private LocalDate suggestedDate;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Risk> risks  = new ArrayList<Risk>();
    @ManyToOne
    @JoinColumn(name="assetType_id")
    private AssetType assetType;
    @OneToMany(mappedBy="assetType",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AssetType> subAssetTypes=new ArrayList<AssetType>();



    public AssetType(String name, Long countryId, SuggestedDataStatus suggestedDataStatus) {
        this.name = name;
        this.countryId = countryId;
        this.suggestedDataStatus=suggestedDataStatus;
    }


    public List<Risk> getRisks() {
        return risks;
    }

    public void setRisks(List<Risk> risks) {
        this.risks = risks;
    }

    public AssetType(String name) {
        this.name = name;
    }

    public LocalDate getSuggestedDate() { return suggestedDate; }

    public void setSuggestedDate(LocalDate suggestedDate) { this.suggestedDate = suggestedDate; }

    public SuggestedDataStatus getSuggestedDataStatus() { return suggestedDataStatus; }

    public void setSuggestedDataStatus(SuggestedDataStatus suggestedDataStatus) { this.suggestedDataStatus = suggestedDataStatus; }

    public boolean isHasSubAsset() { return hasSubAsset; }

    public void setHasSubAsset(boolean hasSubAsset) { this.hasSubAsset = hasSubAsset; }

    public boolean isSubAssetType() { return subAssetType; }

    public void setSubAssetType(boolean subAssetType) { this.subAssetType = subAssetType; }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }

    public List<AssetType> getSubAssetTypes() { return subAssetTypes; }

    public Long getOrganizationId() { return organizationId; }

    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }

    public void setSubAssetTypes(List<AssetType> subAssetTypes) {
        this.subAssetTypes = subAssetTypes;
    }

    public AssetType() {
    }

    List<Risk> getRiskOfAssetTypeAndSubAssetType(){
        List<Risk> risks = this.getRisks();
        this.getSubAssetTypes().forEach( subAssetType -> {
            risks.addAll(subAssetType.getRisks());
        });
        return risks;
    }

    @Override
    public void delete() {
        this.setDeleted(true);
        this.getRisks().forEach( assetTypeRisk -> {
            assetTypeRisk.delete();
        });
        if(!this.getSubAssetTypes().isEmpty()) {
            this.getSubAssetTypes().forEach(subAssetType -> {
                subAssetType.delete();
            });
        }
    }
}
