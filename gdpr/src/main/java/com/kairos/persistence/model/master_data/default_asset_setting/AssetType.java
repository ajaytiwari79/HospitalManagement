package com.kairos.persistence.model.master_data.default_asset_setting;

import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Document(collection = "asset_type")
public class AssetType extends MongoBaseEntity {

    @NotBlank(message = "Name can't be empty or null")
    @Pattern(message = "Numbers and Special characters are not allowed for Name", regexp = "^[a-zA-Z\\s]+$")
    private String name;
    private Long countryId;
    private boolean subAsset;
    private boolean hasSubAsset;
    private SuggestedDataStatus suggestedDataStatus;
    private LocalDate suggestedDate;
    private List<BigInteger> risks;
    private List<BigInteger> subAssetTypes=new ArrayList<>();


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

    public List<BigInteger> getSubAssetTypes() { return subAssetTypes; }

    public void setSubAssetTypes(List<BigInteger> subAssetTypes) { this.subAssetTypes = subAssetTypes; }

    public boolean isSubAsset() { return subAsset; }

    public void setSubAsset(boolean subAsset) { this.subAsset = subAsset; }

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

    public List<BigInteger> getRisks() { return risks; }

    public void setRisks(List<BigInteger> risks) { this.risks = risks; }


    public AssetType() {
    }
}
