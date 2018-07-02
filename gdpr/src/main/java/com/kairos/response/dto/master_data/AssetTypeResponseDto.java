package com.kairos.response.dto.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigInteger;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetTypeResponseDto {


    private BigInteger id;

    private String name;

    private List<AssetTypeResponseDto> subAssetTypes;

    private Boolean isSubAsset;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AssetTypeResponseDto> getSubAssetTypes() {
        return subAssetTypes;
    }

    public void setSubAssetTypes(List<AssetTypeResponseDto> subAssetTypes) {
        this.subAssetTypes = subAssetTypes;
    }

    public Boolean getSubAsset() {
        return isSubAsset;
    }

    public void setSubAsset(Boolean subAsset) {
        isSubAsset = subAsset;
    }
}
