package com.kairos.persistance.model.master_data.default_asset_setting;

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

    private Boolean isSubAsset=false ;

    private Boolean hasSubAsset=false;

    public Boolean getHasSubAsset() {
        return hasSubAsset;
    }

    public void setHasSubAsset(Boolean hasSubAsset) {
        this.hasSubAsset = hasSubAsset;
    }

    private List<BigInteger> subAssetTypes;

    public List<BigInteger> getSubAssetTypes() {
        return subAssetTypes;
    }

    public void setSubAssetTypes(List<BigInteger> subAssetTypes) {
        this.subAssetTypes = subAssetTypes;
    }

    public Boolean getSubAsset() {
        return isSubAsset;
    }

    public void setSubAsset(Boolean subAsset) {
        isSubAsset = subAsset;
    }


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
