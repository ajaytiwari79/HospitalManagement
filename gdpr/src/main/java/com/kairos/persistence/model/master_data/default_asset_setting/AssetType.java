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

    @NotBlank(message = "error.message.name.notNull.orEmpty or null")
    @Pattern(message = "error.message.name.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String name;
    private Long countryId;
    private Long organizationId;
    private boolean isSubAssetType;
    private boolean hasSubAssetType;
    private SuggestedDataStatus suggestedDataStatus;
    private LocalDate suggestedDate;
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "assetType_id")
    private List<Risk> risks  = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name="assetType_id")
    private AssetType assetType;
    @OneToMany(mappedBy="assetType",cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<AssetType> subAssetTypes= new ArrayList<>();



    public AssetType(String name, Long countryId, SuggestedDataStatus suggestedDataStatus) {
        this.name = name;
        this.countryId = countryId;
        this.suggestedDataStatus=suggestedDataStatus;
    }

    public AssetType(@NotBlank(message = "error.message.name.notNull.orEmpty or null") @Pattern(message = "error.message.name.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$") String name,Long organizationId, boolean isSubAssetType) {
        this.name = name;
        this.organizationId = organizationId;
        this.isSubAssetType = isSubAssetType;
    }

    public AssetType( Long countryId,  SuggestedDataStatus suggestedDataStatus) {
        this.countryId = countryId;
        this.suggestedDataStatus = suggestedDataStatus;
    }

    public List<Risk> getRisks() {
        return risks;
    }

    public void setRisks(List<Risk> risks) {
        this.risks = risks;
    }

    public LocalDate getSuggestedDate() { return suggestedDate; }

    public void setSuggestedDate(LocalDate suggestedDate) { this.suggestedDate = suggestedDate; }

    public SuggestedDataStatus getSuggestedDataStatus() { return suggestedDataStatus; }

    public void setSuggestedDataStatus(SuggestedDataStatus suggestedDataStatus) { this.suggestedDataStatus = suggestedDataStatus; }

    public boolean isHasSubAssetType() { return hasSubAssetType; }

    public void setHasSubAssetType(boolean hasSubAssetType) { this.hasSubAssetType = hasSubAssetType; }

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

    public boolean isSubAssetType() {
        return isSubAssetType;
    }

    public void setSubAssetType(boolean subAssetType) {
        isSubAssetType = subAssetType;
    }

    public AssetType() {
    }

    @Override
    public void delete() {
        this.setDeleted(true);
        this.getRisks().forEach(BaseEntity::delete);
        if(!this.getSubAssetTypes().isEmpty()) {
            this.getSubAssetTypes().forEach(AssetType::delete);
        }
    }
}
