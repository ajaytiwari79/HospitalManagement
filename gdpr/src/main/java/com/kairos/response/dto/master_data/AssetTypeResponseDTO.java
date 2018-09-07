package com.kairos.response.dto.master_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.gdpr.data_inventory.RiskDTO;
import com.kairos.persistance.model.risk_management.Risk;
import com.kairos.response.dto.common.RiskResponseDTO;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetTypeResponseDTO {


    private BigInteger id;

    private String name;

    private List<RiskResponseDTO> risks=new ArrayList<>();

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

    public List<RiskResponseDTO> getRisks() { return risks; }

    public void setRisks(List<RiskResponseDTO> risks) { this.risks = risks; }

    public void setHasSubAsset(Boolean hasSubAsset) {
        this.hasSubAsset = hasSubAsset;
    }
}
