package com.kairos.response.dto.master_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.gdpr.data_inventory.OrganizationLevelRiskDTO;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssetTypeResponseDTO {

    private Long id;
    private String name;
    private List<AssetTypeResponseDTO> subAssetTypes=new ArrayList<>();
    private List<OrganizationLevelRiskDTO> risks=new ArrayList<>();
    private Boolean hasSubAsset;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public List<OrganizationLevelRiskDTO> getRisks() {
        return risks;
    }

    public void setRisks(List<OrganizationLevelRiskDTO> risks) {
        this.risks = risks;
    }
}
