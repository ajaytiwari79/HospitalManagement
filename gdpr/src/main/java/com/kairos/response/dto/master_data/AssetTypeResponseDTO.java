package com.kairos.response.dto.master_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssetTypeResponseDTO {

    private BigInteger id;
    private String name;
    private List<AssetTypeResponseDTO> subAssetTypes=new ArrayList<>();
    private Boolean hasSubAsset;


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

    public List<AssetTypeResponseDTO> getSubAssetTypes() {
        return subAssetTypes;
    }

    public void setSubAssetTypes(List<AssetTypeResponseDTO> subAssetTypes) {
        this.subAssetTypes = subAssetTypes;
    }

    public Boolean getHasSubAsset() {
        return hasSubAsset;
    }

    public void setHasSubAsset(Boolean hasSubAsset) {
        this.hasSubAsset = hasSubAsset;
    }
}
