package com.kairos.response.dto.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.response.dto.common.RiskBasicResponseDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetTypeRiskResponseDTO {

    @NotNull
    private Long id;
    @NotBlank
    private String name;
    private List<RiskBasicResponseDTO> risks=new ArrayList<>();
    private Boolean hasSubAsset;
    private List<AssetTypeRiskResponseDTO> subAssetTypes=new ArrayList<>();


    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public List<RiskBasicResponseDTO> getRisks() { return risks; }

    public void setRisks(List<RiskBasicResponseDTO> risks) { this.risks = risks; }

    public Boolean getHasSubAsset() { return hasSubAsset; }

    public void setHasSubAsset(Boolean hasSubAsset) { this.hasSubAsset = hasSubAsset; }

    public List<AssetTypeRiskResponseDTO> getSubAssetTypes() { return subAssetTypes; }

    public void setSubAssetTypes(List<AssetTypeRiskResponseDTO> subAssetTypes) { this.subAssetTypes = subAssetTypes; }
}
