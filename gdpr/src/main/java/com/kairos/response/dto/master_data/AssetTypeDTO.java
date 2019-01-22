package com.kairos.response.dto.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.response.dto.common.RiskBasicResponseDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetTypeDTO {

    @NotNull
    private Long assetTypeId;
    @NotBlank
    private String assetTypeName;

    boolean isSubAssetType;

    public Long getAssetTypeId() {
        return assetTypeId;
    }

    public void setAssetTypeId(Long assetTypeId) {
        this.assetTypeId = assetTypeId;
    }

    public String getAssetTypeName() {
        return assetTypeName;
    }

    public void setAssetTypeName(String assetTypeName) {
        this.assetTypeName = assetTypeName;
    }

    public boolean isSubAssetType() {
        return isSubAssetType;
    }

    public void setSubAssetType(boolean subAssetType) {
        isSubAssetType = subAssetType;
    }

    public AssetTypeDTO(@NotNull Long assetTypeId, @NotBlank String assetTypeName, boolean isSubAssetType) {
        this.assetTypeId = assetTypeId;
        this.assetTypeName = assetTypeName;
        this.isSubAssetType = isSubAssetType;
    }
}
