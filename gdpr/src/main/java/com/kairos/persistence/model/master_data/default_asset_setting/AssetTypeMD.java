package com.kairos.persistence.model.master_data.default_asset_setting;

import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.risk_management.Risk;
import com.kairos.persistence.model.risk_management.RiskMD;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
public class AssetTypeMD extends BaseEntity {

    @NotBlank(message = "Name can't be empty or null")
    @Pattern(message = "Numbers and Special characters are not allowed for Name", regexp = "^[a-zA-Z\\s]+$")
    private String name;
    private Long countryId;
    private boolean subAssetType;
    private boolean hasSubAsset;
    private SuggestedDataStatus suggestedDataStatus;
    private LocalDate suggestedDate;

    @OneToMany(mappedBy = "assetType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RiskMD> risks  = new ArrayList<RiskMD>();

    @ManyToOne
    @JoinColumn(name="assetType_id")
    private AssetTypeMD assetType;

    @OneToMany(mappedBy="assetType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AssetTypeMD> subAssetTypes=new ArrayList<AssetTypeMD>();



    public AssetTypeMD(String name, Long countryId, SuggestedDataStatus suggestedDataStatus) {
        this.name = name;
        this.countryId = countryId;
        this.suggestedDataStatus=suggestedDataStatus;
    }


    public List<RiskMD> getRisks() {
        return risks;
    }

    public void setRisks(List<RiskMD> risks) {
        this.risks = risks;
    }

    public AssetTypeMD(String name) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AssetTypeMD getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetTypeMD assetType) {
        this.assetType = assetType;
    }

    public List<AssetTypeMD> getSubAssetTypes() {
        return subAssetTypes;
    }

    public void setSubAssetTypes(List<AssetTypeMD> subAssetTypes) {
        this.subAssetTypes = subAssetTypes;
    }

    public AssetTypeMD() {
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
