package com.kairos.response.dto.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.response.dto.common.RiskResponseDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetTypeRiskResponseDTO {

    @NotNull
    private BigInteger id;
    @NotBlank
    private String name;
    private List<RiskResponseDTO> risks=new ArrayList<>();
    private Boolean hasSubAsset;
    private List<BigInteger> subAssetTypes=new ArrayList<>();


    public BigInteger getId() { return id; }

    public void setId(BigInteger id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public List<RiskResponseDTO> getRisks() { return risks; }

    public void setRisks(List<RiskResponseDTO> risks) { this.risks = risks; }

    public Boolean getHasSubAsset() { return hasSubAsset; }

    public void setHasSubAsset(Boolean hasSubAsset) { this.hasSubAsset = hasSubAsset; }

    public List<BigInteger> getSubAssetTypes() { return subAssetTypes; }

    public void setSubAssetTypes(List<BigInteger> subAssetTypes) { this.subAssetTypes = subAssetTypes; }
}
