package com.kairos.persistance.model.master_data.default_asset_setting;

import com.kairos.enums.SuggestedDataStatus;
import com.kairos.persistance.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.List;


@Document(collection = "asset_type")
public class AssetType extends MongoBaseEntity {

    @NotBlank(message = "Name can't be empty or null")
    @Pattern(message = "Numbers and Special characters are not allowed for Name", regexp = "^[a-zA-Z\\s]+$")
    private String name;

    private Long countryId;

    private boolean subAsset=false ;

    private boolean hasSubAsset=false;

    private String suggestedDataStatus;

    public String getSuggestedDataStatus() { return suggestedDataStatus; }

    public void setSuggestedDataStatus(String suggestedDataStatus) { this.suggestedDataStatus = suggestedDataStatus; }

    public boolean isHasSubAsset() { return hasSubAsset; }

    public void setHasSubAsset(boolean hasSubAsset) { this.hasSubAsset = hasSubAsset; }

    private List<BigInteger> subAssetTypes;

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
}
