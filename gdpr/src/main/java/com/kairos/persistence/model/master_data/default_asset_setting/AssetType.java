package com.kairos.persistence.model.master_data.default_asset_setting;

import com.kairos.enums.gdpr.SuggestedDataStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;



public class AssetType {

    @NotBlank(message = "Name can't be empty or null")
    @Pattern(message = "Numbers and Special characters are not allowed for Name", regexp = "^[a-zA-Z\\s]+$")
    private String name;
    private Long countryId;
    private boolean subAssetType;
    private boolean hasSubAsset;
    private SuggestedDataStatus suggestedDataStatus;
    private LocalDate suggestedDate;
    private Set<BigInteger> risks=new HashSet<>();
    private Set<BigInteger> subAssetTypes=new HashSet<>();


    public AssetType(String name, Long countryId, SuggestedDataStatus suggestedDataStatus) {
        this.name = name;
        this.countryId = countryId;
        this.suggestedDataStatus=suggestedDataStatus;
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

    public Set<BigInteger> getSubAssetTypes() { return subAssetTypes; }

    public void setSubAssetTypes(Set<BigInteger> subAssetTypes) { this.subAssetTypes = subAssetTypes; }

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

    public Set<BigInteger> getRisks() { return risks; }

    public void setRisks(Set<BigInteger> risks) { this.risks = risks; }

    public AssetType() {
    }
}
